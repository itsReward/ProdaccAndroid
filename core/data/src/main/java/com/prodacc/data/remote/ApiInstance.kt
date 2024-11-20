package com.prodacc.data.remote

import com.prodacc.data.remote.services.LogInService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiInstance {

    private val retrofitBuilder: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://192.168.0.123:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val logInService: LogInService by lazy {
        retrofitBuilder.create(LogInService::class.java)
    }

}