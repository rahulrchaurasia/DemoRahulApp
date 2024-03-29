package com.policyboss.demoandroidapp.UI.NavigationComponent.advanceDemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.policyboss.demoandroidapp.R
import com.policyboss.demoandroidapp.databinding.FragmentViewBalanceBinding
/************************************************************************
  popBackStack()
  This method is used to pop the topmost fragment from the back stack, essentially
  going back to the previous fragment

************************************************************************/

class ViewBalanceFragment : Fragment() {


    private var _binding : FragmentViewBalanceBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_view_balance, container, false)

        _binding = FragmentViewBalanceBinding.inflate(inflater,container,false)
       return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnDone.setOnClickListener{
           // region popBackStack
           /*
            This method is used to pop the topmost fragment from the back stack, essentially
            going back to the previous fragment

            */
          //  findNavController().popBackStack()
         //  findNavController().popBackStack(R.id.homeDashBoardFragment,true)

            findNavController().popBackStack(R.id.homeDashBoardFragment,false)


        }

       // requireActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = ViewBalanceFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}