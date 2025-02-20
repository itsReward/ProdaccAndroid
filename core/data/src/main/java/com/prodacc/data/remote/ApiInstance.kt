package com.prodacc.data.remote

/*
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.prodacc.data.NetworkManager
import com.prodacc.data.remote.services.ClientService
import com.prodacc.data.remote.services.CommentService
import com.prodacc.data.remote.services.ControlChecklistService
import com.prodacc.data.remote.services.EmployeeService
import com.prodacc.data.remote.services.JobCardReportService
import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.JobCardStatusService
import com.prodacc.data.remote.services.JobCardTechniciansService
import com.prodacc.data.remote.services.LogInService
import com.prodacc.data.remote.services.ProductService
import com.prodacc.data.remote.services.ServiceChecklistService
import com.prodacc.data.remote.services.StateChecklistService
import com.prodacc.data.remote.services.TimesheetService
import com.prodacc.data.remote.services.UserService
import com.prodacc.data.remote.services.VehicleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
*/


/*object ApiInstance : NetworkManager.NetworkChangeListener {
    private val logger = Logger.getLogger(ApiInstance::class.java.name)
    var url: String = ""
    private var contextRef: WeakReference<Context>? = null

    fun initialize(context: Context) {
        contextRef = WeakReference(context.applicationContext)
        NetworkManager.getInstance(context).addNetworkChangeListener(this)
        updateBaseUrl()
    }

    override fun onNetworkChanged() {
        contextRef?.get()?.let { updateBaseUrl() }
        Log.e("Api Connection Change", "START")

    }

    private fun updateBaseUrl() {
        contextRef?.get()?.let { context ->
            CoroutineScope(Dispatchers.IO).launch {
                val baseUrl = NetworkManager.getInstance(context).getBaseUrl()
                url = baseUrl
                _retrofitBuilder = createRetrofitBuilder(context, baseUrl)
                initializeServices()
            }
        }
    }

    // Api Connection Initialisation
    private fun initializeServices() {
        _logInService = _retrofitBuilder.create(LogInService::class.java)
        _jobCardService = _retrofitBuilder.create(JobCardService::class.java)
        _vehicleService = _retrofitBuilder.create(VehicleService::class.java)
        _clientService = _retrofitBuilder.create(ClientService::class.java)
        _employeeService = _retrofitBuilder.create(EmployeeService::class.java)
        _userService = _retrofitBuilder.create(UserService::class.java)
        _controlChecklistService = _retrofitBuilder.create(ControlChecklistService::class.java)
        _jobCardReportService = _retrofitBuilder.create(JobCardReportService::class.java)
        _jobCardStatusService = _retrofitBuilder.create(JobCardStatusService::class.java)
        _jobCardTechniciansService = _retrofitBuilder.create(JobCardTechniciansService::class.java)
        _serviceChecklistService = _retrofitBuilder.create(ServiceChecklistService::class.java)
        _stateChecklistService = _retrofitBuilder.create(StateChecklistService::class.java)
        _timesheetService = _retrofitBuilder.create(TimesheetService::class.java)
        _commentService = _retrofitBuilder.create(CommentService::class.java)
        _productService = _retrofitBuilder.create(ProductService::class.java)

    }

    val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDateTime::class.java,
            JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            }
        )
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            val dateString = json.asString
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        })
        .create()

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
    private var _controlChecklistService =
        _retrofitBuilder.create(ControlChecklistService::class.java)
    private var _jobCardReportService = _retrofitBuilder.create(JobCardReportService::class.java)
    private var _jobCardStatusService = _retrofitBuilder.create(JobCardStatusService::class.java)
    private var _jobCardTechniciansService =
        _retrofitBuilder.create(JobCardTechniciansService::class.java)
    private var _serviceChecklistService =
        _retrofitBuilder.create(ServiceChecklistService::class.java)
    private var _stateChecklistService = _retrofitBuilder.create(StateChecklistService::class.java)
    private var _timesheetService = _retrofitBuilder.create(TimesheetService::class.java)
    private var _commentService = _retrofitBuilder.create(CommentService::class.java)
    private var _productService = _retrofitBuilder.create(ProductService::class.java)

    // Getter for retrofitBuilder that returns the current instance
    val retrofitBuilder: Retrofit
        get() = _retrofitBuilder

    // Getters for services
    val logInService: LogInService get() = _logInService
    val jobCardService: JobCardService get() = _jobCardService
    val vehicleService: VehicleService get() = _vehicleService
    val clientService: ClientService get() = _clientService
    val employeeService: EmployeeService get() = _employeeService
    val userService: UserService get() = _userService
    val controlChecklistService: ControlChecklistService get() = _controlChecklistService
    val jobCardReportService: JobCardReportService get() = _jobCardReportService
    val jobCardStatusService: JobCardStatusService get() = _jobCardStatusService
    val jobCardTechniciansService: JobCardTechniciansService get() = _jobCardTechniciansService
    val serviceChecklistService: ServiceChecklistService get() = _serviceChecklistService
    val stateChecklistService: StateChecklistService get() = _stateChecklistService
    val timesheetService: TimesheetService get() = _timesheetService
    val commentService: CommentService get() = _commentService
    val productService: ProductService get() = _productService


    // Default builder without caching (for initial setup)
    private fun createDefaultRetrofitBuilder(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Add Authorization header if token is available
                TokenManager.getToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
                }

                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Method to create Retrofit builder with caching
    private fun createRetrofitBuilder(context: Context, baseUrl: String): Retrofit {
        // Create cache
        val cacheSize = 500L * 1024L * 1024L // 10 MiB
        val httpCacheDirectory = File(context.cacheDir, "http-cache")
        val cache = Cache(httpCacheDirectory, cacheSize)

        val client = OkHttpClient.Builder()
            .cache(cache) // Add cache to OkHttpClient
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Add Authorization header if token is available
                TokenManager.getToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
                }

                // Add caching headers
                requestBuilder.addHeader(
                    "Cache-Control", "public, max-age=2"// Cache for 60 seconds
                ).addHeader("Content-Type", "application/json")

                chain.proceed(requestBuilder.build())
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=300").build()
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun cleanup() {
        contextRef?.get()?.let { context ->
            NetworkManager.getInstance(context).removeNetworkChangeListener(this)
        }
        contextRef = null
    }

}*/
