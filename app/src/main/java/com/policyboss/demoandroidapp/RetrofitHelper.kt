package com.policyboss.demoandroidapp

import com.policyboss.demoandroidapp.LoginModule.API.APIService

import com.google.gson.GsonBuilder
import com.policyboss.demoandroidapp.AdvanceDemo.API.QuoteAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

   // val BASE_URL = "http://transactionapi.tech-sevenpay.com"

    val BASE_URL = "https://api.quotable.io/"

    const val token = "1234567890"
    internal var restAdapter: Retrofit? = null




    private val retrofitInstance by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }



    val retrofitLoginApi by lazy {
        retrofitInstance.create(APIService::class.java)

    }

    val retrofitQuoteApi by lazy {
        retrofitInstance.create(QuoteAPI::class.java)

    }



}