package com.policyboss.demoandroidapp.ViewPagerDemo.ViewPagerWithProgress.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.policyboss.demoandroidapp.Constant
import com.policyboss.demoandroidapp.ViewPagerDemo.ViewPagerWithProgress.adapter.ViewPagerWithProgressAdapter
import com.policyboss.demoandroidapp.ViewPagerDemo.ViewPagerWithProgress.adapter.ViewPager_LinearProgressAdapter
import com.policyboss.demoandroidapp.ViewPagerDemo.model.FoodEntity
import com.policyboss.demoandroidapp.databinding.ActivityViewPagerWithProgressBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//*************************************************************************
//Note :

//1> we have to match timming of Activity and Adapter same here we take 5 sec

//2>  The issue you're facing might be related to the fact that a canceled Job cannot be restarted.
// Once a Job is canceled, it cannot be used again. To achieve the desired behavior of
// stopping and restarting the auto-scroll functionality, you can create a new Job instance
// when restarting the auto-scroll.

//3>  we get diff pos in onPageScrollStateChanged and onPageSelected

//onPageScrollStateChanged uses viewPager.currentItem to access the foodList item, which might not be updated yet during scrolling.
//onPageSelected uses the provided position which reflects the final, selected item after scrolling.
//*************************************************************************

class ViewPagerWithProgressActivity : AppCompatActivity() {

    lateinit var binding : ActivityViewPagerWithProgressBinding

    var foodList : MutableList<FoodEntity> = ArrayList<FoodEntity>()
    private lateinit var viewPager: ViewPager2
    private lateinit var adapterViewPager : ViewPagerWithProgressAdapter

    private lateinit var adpaterLinearProg : ViewPager_LinearProgressAdapter

    private var isUserScrolling = false


    private var autoScrollJob: Job? = null  // Make autoScrollJob nullable


    private var lastUserScrollTime = 0L // Track last user interaction time
    private val MIN_SCROLL_INTERVAL = 1000L // Minimum time in milliseconds after user stops dragging


    private val DEFAULT_SCROLL_INTERVAL = 5000L // Default auto-scroll delay (consider user preferences)

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){


        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            Log.d(Constant.TAG,"onPageSelected : $position " )
           //Note : uses the provided position which reflects the final, selected item after scrolling
            if (!isUserScrolling) {

                val foodEntity : FoodEntity =  foodList[position]
                // adpaterLinearProg.updateProgressAnimations(foodEntity)
                foodEntity?.let { // Update progress only if item exists
                    adpaterLinearProg.updateProgressAnimations(it)
                }


            }

        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
     //onPageScrollStateChanged uses viewPager.currentItem to access the foodList item which might not be updated yet during scrolling.
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    // User started scrolling
                    isUserScrolling = true
                    stopAutoScroll()
                    adpaterLinearProg.hideProgressAnimations()
                  //  Log.d(Constant.TAG,"User Scroll : $isUserScrolling cancel Auto Scroll Linear-Progress" )
                   // lastUserScrollTime = System.currentTimeMillis() // Update time on drag

                }
                ViewPager2.SCROLL_STATE_IDLE -> {
                    // User finished scrolling

                    isUserScrolling = false
                    Log.d(Constant.TAG,"SCROLL_STATE_IDLE called  :  ${viewPager.currentItem}" )


                        if (autoScrollJob?.isActive != true){
                           startAutoScroll()           // Restart auto-scroll after minimum time interval
                          //  startAutoScroll(startPosition =  viewPager.currentItem,_restartImmediate = true)
                           val foodEntity  = foodList.get( viewPager.currentItem)
                            foodEntity?.let { // Update progress only if item exists
                                adpaterLinearProg.updateProgressAnimations(it)
                            }
                        }


                }
            }

        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerWithProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getfoodList()
        init()
        // Start auto-scrolling when the activity is created
        startAutoScroll()
         //adpaterLinearProg.updateProgressAnimations(foodList.first())

       binding.ivClose.setOnClickListener{

           this@ViewPagerWithProgressActivity.finish()
       }

    }
    fun  init() {



        viewPager = binding.viewPager

        viewPager.offscreenPageLimit = 1
        viewPager.clipChildren = false
        viewPager.clipToPadding = false
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        adapterViewPager = ViewPagerWithProgressAdapter(context = this, list = foodList){ entity ,pos ->



        }
        viewPager.adapter = adapterViewPager



        viewPager.registerOnPageChangeCallback(onPageChangeCallback)

        setProgressAdapter()


    }

    fun getfoodList(){

        foodList = mutableListOf(
            FoodEntity(1, "This perfectly thin cut just melts in your mouth.", "https://seanallen-course-backend.herokuapp.com/images/appetizers/asian-flank-steak.jpg",0.20f,"Tandoor") ,
            FoodEntity(2, "Seasoned shrimp from the depths of the Atlantic Ocean.", "https://seanallen-course-backend.herokuapp.com/images/appetizers/blackened-shrimp.jpg",0.80f, "Prawns") ,
            FoodEntity(3, "The tasty bites of chicken have just the right amount of kick to them.", "https://seanallen-course-backend.herokuapp.com/images/appetizers/buffalo-chicken-bites.jpg",0.10f,"Chicken Wings"),
            //FoodEntity(4, "It's really hard to keep coming up with these descriptions.", "https://seanallen-course-backend.herokuapp.com/images/appetizers/chicken-dumplings.jpg",0.55f)


        )
    }



    private fun  startAutoScroll() {



        if ( autoScrollJob?.isActive == true) {

            // stopAutoScroll()
            return
        }
        stopAutoScroll()

        autoScrollJob = lifecycleScope.launch(Dispatchers.Main) {

            while (isActive) {
                // Skip delay only if restartImmediate is true

                delay(DEFAULT_SCROLL_INTERVAL) //  5 sec Adjust the delay for your needs

                withContext(Dispatchers.Main) {

                    // viewPager.currentItem = (viewPager.currentItem + 1) % viewPager.adapter?.itemCount!!

                    if (viewPager.currentItem < viewPager.adapter?.itemCount!! - 1) {
                        viewPager.currentItem++
                    } else {
                        viewPager.currentItem = 0
                    }

                }
            }
        }


    }


    private fun  startAutoScroll_OLD( startPosition: Int = -1, _restartImmediate : Boolean = false) {


         var restartImmediate = _restartImmediate
        // The launch function actually returns a Job, so we return it here

        if ( autoScrollJob?.isActive == true) {

           // stopAutoScroll()
            return
        }
        stopAutoScroll()

        autoScrollJob = lifecycleScope.launch(Dispatchers.Main) {

                while (isActive) {
                    // Skip delay only if restartImmediate is true
                    if (restartImmediate) {

                       // delay(MIN_SCROLL_INTERVAL)
                        restartImmediate = false
                    } else {
                        delay(DEFAULT_SCROLL_INTERVAL) //  5 sec Adjust the delay for your needs
                    }

                    withContext(Dispatchers.Main) {

                        // viewPager.currentItem = (viewPager.currentItem + 1) % viewPager.adapter?.itemCount!!

                        if (viewPager.currentItem < viewPager.adapter?.itemCount!! - 1) {
                            viewPager.currentItem++
                        } else {
                            viewPager.currentItem = 0
                        }


                        restartImmediate = false





                    }
                }
            }


    }


    fun setProgressAdapter() {

        val layoutManager = GridLayoutManager(this, 3) // 3 columns
        binding.rvProgress.layoutManager = layoutManager

        adpaterLinearProg = ViewPager_LinearProgressAdapter(context = this, list = foodList)

        binding.rvProgress.adapter = adpaterLinearProg
        binding.rvProgress.setHasFixedSize(true);
    }

    fun stopAutoScroll() {
        autoScrollJob?.cancel()

    }
    override fun onDestroy() {
        super.onDestroy()
        // Cancel the auto-scroll job to avoid memory leaks
        stopAutoScroll()

    }
}