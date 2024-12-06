package com.prodacc.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.prodacc.data.remote.services.ClientService
import com.prodacc.data.remote.services.EmployeeService
import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.LogInService
import com.prodacc.data.remote.services.UserService
import com.prodacc.data.remote.services.VehicleService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


object ApiInstance {
    @Volatile
    var BASE_URL = "http://192.168.138.124:5000"
        set

    // Add a method to initialize with context
    fun initialize(context: Context, baseUrl: String = BASE_URL) {
        BASE_URL = baseUrl
        // Recreate the Retrofit instance with the provided context
        _retrofitBuilder = createRetrofitBuilder(context)

        // Recreate services
        _logInService = _retrofitBuilder.create(LogInService::class.java)
        _jobCardService = _retrofitBuilder.create(JobCardService::class.java)
        _vehicleService = _retrofitBuilder.create(VehicleService::class.java)
        _clientService = _retrofitBuilder.create(ClientService::class.java)
        _employeeService = _retrofitBuilder.create(EmployeeService::class.java)
        _userService = _retrofitBuilder.create(UserService::class.java)
    }

    val gson = GsonBuilder().registerTypeAdapter(
        LocalDateTime::class.java,
        JsonDeserializer { json, _, _ ->
            val dateString = json.asString
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }).create()

    // Use a private backing field for retrofitBuilder
    private var _retrofitBuilder: Retrofit = createDefaultRetrofitBuilder()

    // Use a private backing field for services
    private var _logInService: LogInService = _retrofitBuilder.create(LogInService::class.java)
    private var _jobCardService: JobCardService =
        _retrofitBuilder.create(JobCardService::class.java)
    private var _vehicleService: VehicleService =
        _retrofitBuilder.create(VehicleService::class.java)
    private var _clientService: ClientService = _retrofitBuilder.create(ClientService::class.java)
    private var _employeeService: EmployeeService =
        _retrofitBuilder.create(EmployeeService::class.java)
    private var _userService = _retrofitBuilder.create(UserService::class.java)

    // Getter for retrofitBuilder that returns the current instance
    val retrofitBuilder: Retrofit
        get() = _retrofitBuilder

    // Getters for services
    val logInService: LogInService
        get() = _logInService

    val jobCardService: JobCardService
        get() = _jobCardService

    val vehicleService: VehicleService
        get() = _vehicleService

    val clientService: ClientService
        get() = _clientService

    val employeeService: EmployeeService
        get() = _employeeService

    val userService: UserService
        get() = _userService


    // Default builder without caching (for initial setup)
    private fun createDefaultRetrofitBuilder(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // Add Authorization header if token is available
            TokenManager.getToken()?.let { token ->
                println("Authorization Bearer ${token.accessToken}")
                requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
            }

            chain.proceed(requestBuilder.build())
        }.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    // Method to create Retrofit builder with caching
    private fun createRetrofitBuilder(context: Context): Retrofit {
        // Create cache
        val cacheSize = 500L * 1024L * 1024L // 10 MiB
        val httpCacheDirectory = File(context.cacheDir, "http-cache")
        val cache = Cache(httpCacheDirectory, cacheSize)

        val client = OkHttpClient.Builder().cache(cache) // Add cache to OkHttpClient
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Add Authorization header if token is available
                TokenManager.getToken()?.let { token ->
                    println("Authorization Bearer ${token.accessToken}")
                    requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
                }

                // Add caching headers
                requestBuilder.addHeader(
                    "Cache-Control", "public, max-age=60"
                ) // Cache for 60 seconds

                chain.proceed(requestBuilder.build())
            }.addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=300").build()
            }.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
}
