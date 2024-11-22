package com.prodacc.data.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.LogInService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

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


    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            // Adjust the parsing format to match your actual JSON
            val dateString = json.asString
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            // Or use a custom formatter if the date format is different
            // LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        })
        .create()


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
                    println("Authorization Bearer ${token.accessToken}")
                    requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
                }

                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}