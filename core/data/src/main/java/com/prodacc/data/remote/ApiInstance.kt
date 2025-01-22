package com.prodacc.data.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.services.ClientService
import com.prodacc.data.remote.services.ControlChecklistService
import com.prodacc.data.remote.services.EmployeeService
import com.prodacc.data.remote.services.JobCardReportService
import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.JobCardStatusService
import com.prodacc.data.remote.services.JobCardTechniciansService
import com.prodacc.data.remote.services.LogInService
import com.prodacc.data.remote.services.ServiceChecklistService
import com.prodacc.data.remote.services.StateChecklistService
import com.prodacc.data.remote.services.TimesheetService
import com.prodacc.data.remote.services.UserService
import com.prodacc.data.remote.services.VehicleService
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.logging.Logger


object ApiInstance {
    private val logger = Logger.getLogger(ApiInstance::class.java.name)

    var WSURL = MutableStateFlow("not changed")

    @Volatile
    var BASE_URL = "http://192.168.122.123:5000"
        set

    //WebSocket
    private var webSocket: WebSocket? = null
    private val webSocketListeners = mutableListOf<WebSocketEventListener>()

    // Interface for WebSocket events
    interface WebSocketEventListener {
        fun onJobCardUpdate(update: WebSocketUpdate)
        fun onWebSocketError(error: Throwable)
    }

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
        _controlChecklistService = _retrofitBuilder.create(ControlChecklistService::class.java)
        _jobCardReportService = _retrofitBuilder.create(JobCardReportService::class.java)
        _jobCardStatusService = _retrofitBuilder.create(JobCardStatusService::class.java)
        _jobCardTechniciansService = _retrofitBuilder.create(JobCardTechniciansService::class.java)
        _serviceChecklistService = _retrofitBuilder.create(ServiceChecklistService::class.java)
        _stateChecklistService = _retrofitBuilder.create(StateChecklistService::class.java)
        _timesheetService = _retrofitBuilder.create(TimesheetService::class.java)

        setupWebSocket()
    }

    fun reconnectWebSocket() {
        setupWebSocket()
    }

    fun sendWebSocketMessage(type: String, data: Any) {
        webSocket?.let { ws ->
            try {
                val jsonObject = JSONObject().apply {
                    put("type", type)
                    put("data", JSONObject(gson.toJson(data)))
                }
                ws.send(jsonObject.toString())
            } catch (e: Exception) {
                Log.e("WebSocket", "Error sending message", e)
            }
        }
    }


    private fun setupWebSocket() {
        val token = TokenManager.getToken()?.accessToken ?: run {
            Log.e("WebSocket", "No token available")
            WSURL.value = "No authentication token"
            return
        }

        val wsUrl = try {
            BASE_URL.replace("http://", "ws://")
                .replace("https://", "wss://")
                .let { "$it/websocket?token=$token" }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error creating WebSocket URL", e)
            return
        }

        WSURL.value = wsUrl
        Log.d("WebSocket", "Attempting connection to: $wsUrl")


        val client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS) // Keep connection alive
            .readTimeout(0, TimeUnit.MILLISECONDS) // Important for WebSocket
            .writeTimeout(0, TimeUnit.MILLISECONDS) // Important for WebSocket
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Origin", BASE_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection established")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val jsonObject = JSONObject(text)
                    val type = jsonObject.getString("type")

                    logger.info("WebSocket Received: $type")

                    when (type) {
                        "NEW_JOB_CARD" -> {
                            logger.info("WebSocket Received: $type")
                            val data = jsonObject.getJSONObject("data").toString()
                            val jobCard = gson.fromJson(data, JobCard::class.java)
                            val update = WebSocketUpdate.JobCardCreated(jobCard.id)
                            webSocketListeners.forEach { listener ->
                                listener.onJobCardUpdate(update)
                            }
                        }
                        "DELETE_JOB_CARD" -> {
                            val jobCardId = UUID.fromString(jsonObject.getString("data"))  // Parse data as string
                            val update = WebSocketUpdate.JobCardDeleted(jobCardId)  // or create a new DeletedJobCard update type
                            webSocketListeners.forEach { listener ->
                                listener.onJobCardUpdate(update)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message", e)
                }

                /*try {
                    val update = gson.fromJson(text, WebSocketUpdate::class.java)
                    webSocketListeners.forEach { listener ->
                        listener.onJobCardUpdate(update)
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message", e)
                }*/
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", """
                WebSocket failure:
                Error: ${t.message}
                Response code: ${response?.code}
                Response message: ${response?.message}
                Response headers: ${response?.headers}
                Full URL: $wsUrl
            """.trimIndent())
                webSocketListeners.forEach { listener ->
                    listener.onWebSocketError(t)
                }
                // Attempt to reconnect after delay
                Handler(Looper.getMainLooper()).postDelayed({
                    setupWebSocket()
                }, 5000)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "WebSocket closed: $reason")
            }
        })
    }

    // Methods to manage WebSocket listeners
    fun addWebSocketListener(listener: WebSocketEventListener) {
        webSocketListeners.add(listener)
    }

    fun removeWebSocketListener(listener: WebSocketEventListener) {
        webSocketListeners.remove(listener)
    }

    // Clean up method
    fun cleanup() {
        webSocket?.close(1000, "App closing")
        webSocket = null
        webSocketListeners.clear()
    }




    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java,
            JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            })
        .registerTypeAdapter(LocalDateTime::class.java,
        JsonDeserializer { json, _, _ ->
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

    private var _controlChecklistService = _retrofitBuilder.create(ControlChecklistService::class.java)
    private var _jobCardReportService = _retrofitBuilder.create(JobCardReportService::class.java)
    private var _jobCardStatusService = _retrofitBuilder.create(JobCardStatusService::class.java)
    private var _jobCardTechniciansService = _retrofitBuilder.create(JobCardTechniciansService::class.java)
    private var _serviceChecklistService = _retrofitBuilder.create(ServiceChecklistService::class.java)
    private var _stateChecklistService = _retrofitBuilder.create(StateChecklistService::class.java)
    private var _timesheetService = _retrofitBuilder.create(TimesheetService::class.java)

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

    val controlChecklistService: ControlChecklistService
        get() = _controlChecklistService

    val jobCardReportService: JobCardReportService
        get() = _jobCardReportService

    val jobCardStatusService: JobCardStatusService
        get() = _jobCardStatusService

    val jobCardTechniciansService: JobCardTechniciansService
        get() = _jobCardTechniciansService

    val serviceChecklistService: ServiceChecklistService
        get() = _serviceChecklistService

    val stateChecklistService: StateChecklistService
        get() = _stateChecklistService

    val timesheetService: TimesheetService
        get() = _timesheetService


    // Default builder without caching (for initial setup)
    private fun createDefaultRetrofitBuilder(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // Add Authorization header if token is available
            TokenManager.getToken()?.let { token ->
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
                    requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
                }

                // Add caching headers
                requestBuilder.addHeader(
                    "Cache-Control", "public, max-age=2"// Cache for 60 seconds
                ).addHeader("Content-Type", "application/json")

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


// Define your WebSocket update data class
sealed class WebSocketUpdate {
    data class JobCardCreated(val jobCardId: UUID) : WebSocketUpdate()
    data class JobCardDeleted(val jobCardId: UUID) : WebSocketUpdate()
    data class JobCardUpdated(val jobCardId: UUID) : WebSocketUpdate()
    data class StatusChanged(val jobCardId: UUID, val newStatus: String) : WebSocketUpdate()
}
