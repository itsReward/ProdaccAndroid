package com.prodacc.data.remote

import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.LogInService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*
object ApiInstance {
    var BASE_URL = "http://192.168.0.123:5000"

    *//*
    for peoduction
    private val retrofitBuilder: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }*//*

    val retrofitBuilder: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val logInService: LogInService by lazy {
        retrofitBuilder.create(LogInService::class.java)
    }

    val jobCardService: JobCardService by lazy {
        retrofitBuilder.create(JobCardService::class.java)
    }

}*/

object ApiInstance {
    @Volatile
    var BASE_URL = "http://192.168.137.123:5000"
        set(value) {
            field = value
            // Invalidate the existing retrofitBuilder
            _retrofitBuilder = createRetrofitBuilder()

            // Recreate services
            _logInService = _retrofitBuilder.create(LogInService::class.java)
            _jobCardService = _retrofitBuilder.create(JobCardService::class.java)
        }

    // Use a private backing field for retrofitBuilder
    private var _retrofitBuilder: Retrofit = createRetrofitBuilder()

    // Use a private backing field for services
    private var _logInService: LogInService = _retrofitBuilder.create(LogInService::class.java)
    private var _jobCardService: JobCardService = _retrofitBuilder.create(JobCardService::class.java)

    // Getter for retrofitBuilder that returns the current instance
    val retrofitBuilder: Retrofit
        get() = _retrofitBuilder

    // Getters for services
    val logInService: LogInService
        get() = _logInService

    val jobCardService: JobCardService
        get() = _jobCardService

    // Separate method to create Retrofit builder
    private fun createRetrofitBuilder(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Add Authorization header if token is available
                TokenManager.getToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer ${token.token}")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Add OkHttpClient to the builder.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}