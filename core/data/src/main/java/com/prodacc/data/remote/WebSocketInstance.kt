package com.prodacc.data.remote

import android.content.Context
import android.util.Log
import com.prodacc.data.NetworkManager
import com.prodacc.data.NotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketInstance @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkManager,
    private val tokenManager: TokenManager,
    private val apiServiceContainer: ApiServiceContainer,
    private val notificationManager: NotificationManager
) : NetworkManager.NetworkChangeListener, TokenManager.TokenListener
{
    private val logger = Logger.getLogger(WebSocketInstance::class.java.name)
    private var contextRef: WeakReference<Context>? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val isConnecting = AtomicBoolean(false)
    private val connectionLock = Mutex()
    private var connectionJob: Job? = null


    private val _webSocketState = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected(""))
    val webSocketState = _webSocketState.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        networkManager.addNetworkChangeListener(this)
        notificationManager.createNotificationChannels(context)
        tokenManager.addTokenChangeListeners(this)
    }

    override fun onTokenUpdate() {
        reconnectWebSocket()
    }

    override fun onNetworkChanged() {
        updateConnection()
    }

    private val _websocketIndicator = MutableStateFlow(true)
    val webSocketIndicator = _websocketIndicator

    fun websocketIndicatorToggle(state: Boolean) {
        _websocketIndicator.value = state
    }

    //WebSocket
    private var webSocket: WebSocket? = null
    private val webSocketListeners = mutableListOf<WebSocketEventListener>()

    // Setting up WebSocket Connection
    private fun setupWebSocket() {
        // Return early if already connecting
        if (!isConnecting.compareAndSet(false, true)) {
            Log.d("WebSocket", "Setup already in progress, skipping")
            return
        }

        val token = tokenManager.getToken()?.accessToken ?: run {
            Log.e("WebSocket", "No token available")
            isConnecting.set(false)
            return
        }

        // Check if we have an existing connection and compare tokens
        webSocket?.let { existingSocket ->
            val currentUrl = existingSocket.request().url.toString()
            val currentToken = currentUrl.substringAfter("token=")

            if (currentToken == token && _webSocketState.value is WebSocketState.Connected) {
                Log.d("WebSocket", "Connection exists with same token, skipping setup")
                isConnecting.set(false)
                return
            }
        }



        serviceScope.launch {
            connectionLock.withLock {
                try {
                    val baseUrl = networkManager.getBaseUrl()

                    val wsUrl = when {
                        baseUrl.startsWith("https://") -> baseUrl.replace("https://", "wss://")
                        baseUrl.startsWith("http://") -> baseUrl.replace("http://", "ws://")
                        else -> baseUrl
                    }.let { "$it/websocket?token=$token" }

                    Log.d("WebSocket", "Attempting connection to: $wsUrl")

                    // Close existing connection before creating new one
                    webSocket?.close(1000, "Setting up new connection")
                    webSocket = null

                    // Small delay to ensure clean closure
                    delay(100)

                    val client = OkHttpClient.Builder()
                        .pingInterval(30, TimeUnit.SECONDS)
                        .readTimeout(3600, TimeUnit.SECONDS) // Changed to SECONDS
                        .writeTimeout(3600, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS) // Reduced timeout
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .header("Upgrade", "websocket")
                                .header("Connection", "Upgrade")
                                .header("Sec-WebSocket-Version", "13")
                                .build()
                            chain.proceed(request)
                        }
                        .retryOnConnectionFailure(true) // Changed to true
                        .build()

                    val request = Request.Builder()
                        .url(wsUrl)
                        .addHeader("Origin", baseUrl)
                        .build()

                    webSocket = client.newWebSocket(request, createWebSocketListener())
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error setting up WebSocket", e)
                    _webSocketState.value = WebSocketState.Error("Error: ${e.message}")
                } finally {
                    isConnecting.set(false)
                }
            }
        }
    }

    private fun createWebSocketListener() = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            _webSocketState.value = WebSocketState.Connected("Connected")
            Log.d("WebSocket", "Connection established")
            websocketIndicatorToggle(true)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val jsonObject = JSONObject(text)
                val type = jsonObject.getString("type")
                val data = jsonObject.getString("data")
                val uuid = UUID.fromString(data)

                // Show notification first
                serviceScope.launch {
                    showNotificationForType(type, uuid)
                }


                logger.info("WebSocket Received: $type")

                when (type) {
                    "NEW_JOB_CARD" -> {
                        logger.info("WebSocket Received: $type")
                        val jobCardId = UUID.fromString(jsonObject.getString("data"))
                        val update = WebSocketUpdate.JobCardCreated(jobCardId)
                        webSocketListeners.forEach { listener -> listener.onWebSocketUpdate(update) }
                    }

                    "DELETE_JOB_CARD" -> {
                        logger.info("WebSocket Received: $type")
                        val jobCardId =
                            UUID.fromString(jsonObject.getString("data"))  // Parse data as string
                        val update =
                            WebSocketUpdate.JobCardDeleted(jobCardId)  // or create a new DeletedJobCard update type
                        webSocketListeners.forEach { listener -> listener.onWebSocketUpdate(update) }
                    }

                    "UPDATE_JOB_CARD" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.JobCardUpdated(
                                    id
                                )
                            )
                        }
                    }

                    "JOB_CARD_STATUS_CHANGED" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.StatusChanged(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_JOB_CARD_REPORT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewReport(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_JOB_CARD_REPORT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateReport(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_JOB_CARD_REPORT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteReport(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_JOB_CARD_TECHNICIAN" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewTechnician(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_JOB_CARD_TECHNICIAN" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteTechnician(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_SERVICE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewServiceChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_SERVICE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateServiceChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_SERVICE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteServiceChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_STATE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewStateChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_STATE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateStateChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_STATE_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteStateChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_CONTROL_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewControlChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_CONTROL_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateControlChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_CONTROL_CHECKLIST" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteControlChecklist(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_TIMESHEET" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewTimesheet(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_TIMESHEET" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateTimesheet(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_TIMESHEET" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteTimesheet(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_CLIENT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewClient(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_CLIENT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateClient(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_CLIENT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteClient(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_VEHICLE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewVehicle(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_VEHICLE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateVehicle(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_VEHICLE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteVehicle(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_EMPLOYEE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewEmployee(
                                    id
                                )
                            )
                        }
                    }

                    "UPDATE_EMPLOYEE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.UpdateEmployee(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_EMPLOYEE" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteEmployee(
                                    id
                                )
                            )
                        }
                    }

                    "NEW_COMMENT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.NewComment(
                                    id
                                )
                            )
                        }
                    }

                    "DELETE_COMMENT" -> {
                        val id = UUID.fromString(jsonObject.getString("data"))
                        webSocketListeners.forEach {
                            it.onWebSocketUpdate(
                                WebSocketUpdate.DeleteComment(
                                    id
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error parsing message", e)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _webSocketState.value = WebSocketState.Error("Error: ${t.message}")
            Log.e(
                "WebSocket", """
                WebSocket failure:
                Error: ${t.message}
                Response code: ${response?.code}
                Response message: ${response?.message}
                Response headers: ${response?.headers}
                Full URL: ${response?.request?.url}
            """.trimIndent()
            )
            webSocketListeners.forEach { listener ->
                listener.onWebSocketError(t)
            }

            // Only attempt reconnect if not already connecting
            if (_webSocketState.value !is WebSocketState.Connected && !isConnecting.get()) {
                serviceScope.launch {
                    delay(5000)
                    if (_webSocketState.value !is WebSocketState.Connected && !isConnecting.get()) {
                        updateConnection()
                    }
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _webSocketState.value = WebSocketState.Disconnected("Closed: $reason")
            Log.e("WebSocket", "WebSocket closed: $reason")
        }
    }

    // Connecting to WebSocket
    fun reconnectWebSocket() {
        Log.e("Launch Reconnect", "Launching reconnection")
        setupWebSocket()
    }

    // Interface for WebSocket events
    interface WebSocketEventListener {
        fun onWebSocketUpdate(update: WebSocketUpdate)
        fun onWebSocketError(error: Throwable)
    }

    // Methods to manage WebSocket listeners
    fun addWebSocketListener(listener: WebSocketEventListener) {
        webSocketListeners.add(listener)
    }

    fun removeWebSocketListener(listener: WebSocketEventListener) {
        webSocketListeners.remove(listener)
    }

    // Send WebSocket Message
    fun sendWebSocketMessage(type: String, data: Any) {
        webSocket?.let { ws ->
            try {
                val jsonObject = JSONObject().apply {
                    put("type", type)
                    put("data", data.toString())
                }
                Log.e("WebSocket", "Sending message: $jsonObject")
                ws.send(jsonObject.toString())
            } catch (e: Exception) {
                Log.e("WebSocket", "Error sending message", e)
            }
        }
    }

    // Clean up method
    fun cleanup() {
        serviceScope.launch {
            connectionLock.withLock {
                connectionJob?.cancel()
                connectionJob = null
                webSocket?.close(1000, "App closing")
                webSocket = null
                webSocketListeners.clear()
            }
        }
        serviceScope.cancel()
        contextRef?.get()?.let { context ->
            NetworkManager.getInstance(context).removeNetworkChangeListener(this)
            tokenManager.removeTokenChangeListener(this)
        }
        contextRef = null
    }


    sealed class WebSocketState {
        data class Connected(val message: String) : WebSocketState()
        data class Disconnected(val message: String) : WebSocketState()
        data class Reconnecting(val message: String) : WebSocketState()
        data class Error(val message: String) : WebSocketState()
    }

    private suspend fun showNotificationForType(type: String, id: UUID) {
        val jobCardName = getJobCardName(id)
        val (title, message) = when (type) {
            "NEW_JOB_CARD" -> Pair(
                "New Job Card",
                "$jobCardName Job Card  has been created"
            )

            "DELETE_JOB_CARD" -> Pair(
                "Job Card Deleted",
                "$jobCardName A Job Card has been deleted"
            )

            "JOB_CARD_STATUS_CHANGED" -> Pair(
                "Status Update",
                "$jobCardName Job Card status has been updated"
            )

            "NEW_JOB_CARD_TECHNICIAN" -> Pair(
                "New Assignment",
                "New technician has been assigned to $jobCardName job card"
            )

            "DELETE_JOB_CARD_TECHNICIAN" -> Pair(
                "Assignment Removed",
                "A technicians has been removed from $jobCardName Job Card "
            )

            "NEW_TIMESHEET" -> Pair(
                "New Timesheet",
                "A new timesheet has been added to $jobCardName Job Card"
            )

            "UPDATE_TIMESHEET" -> Pair(
                "Timesheet Updated",
                "A timesheet has been updated on $jobCardName Job Card"
            )

            "NEW_SERVICE_CHECKLIST" -> Pair(
                "New Service Checklist",
                "A service checklist has been created for $jobCardName Job Card"
            )

            "UPDATE_SERVICE_CHECKLIST" -> Pair(
                "Service Checklist Updated",
                "Service checklist has been updated for $jobCardName Job Card "
            )

            "NEW_STATE_CHECKLIST" -> Pair(
                "New State Checklist",
                "A state checklist has been created for $jobCardName Job Card "
            )

            "UPDATE_STATE_CHECKLIST" -> Pair(
                "State Checklist Updated",
                "State checklist has been updated for $jobCardName Job Card"
            )

            "NEW_CONTROL_CHECKLIST" -> Pair(
                "New Control Checklist",
                "A control checklist has been created for $jobCardName Job Card"
            )

            "UPDATE_CONTROL_CHECKLIST" -> Pair(
                "Control Checklist Updated",
                "Control checklist has been updated for $jobCardName Job Card"
            )

            "NEW_COMMENT" -> Pair(
                "New Comment",
                "New Comment added on $jobCardName Job Card"
            )

            "UPDATE_COMMENT" -> Pair(
                "Comment Updated",
                "Comment updated on $jobCardName Job Card"
            )

            "DELETE_COMMENT" -> Pair(
                "Comment Removed",
                "Comment deleted on $jobCardName Job Card"
            )

            else -> return // No notification for other types
        }

        notificationManager.showNotification(
            title = title,
            message = message,
            type = type,
            entityId = id
        )
    }

    private suspend fun getJobCardName(jobCardId: UUID): String {
        return try {
            val response = apiServiceContainer.jobCardService.getJobCard(jobCardId)
            if (response.isSuccessful) {
                response.body()?.jobCardName ?: "Job Card #$jobCardId"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error fetching job card name", e)
            "Job Card #$jobCardId"
        }
    }

    private fun updateConnection() {
        // Only proceed if not already connecting
        if (!isConnecting.compareAndSet(false, true)) {
            Log.d("WebSocket", "Connection update already in progress, skipping")
            return
        }

        serviceScope.launch {
            connectionLock.withLock {
                try {
                    val currentWsUrl = webSocket?.request()?.url?.toString()
                    val currentToken = currentWsUrl?.substringAfter("token=")
                    val newToken = tokenManager.getToken()?.accessToken

                    // Check if token has changed
                    if (currentToken != newToken) {
                        Log.d("WebSocket", "Token changed, reconnecting")
                        setupWebSocket()
                        return@withLock
                    }

                    // If we're already connected and the socket is alive, don't reconnect
                    if (_webSocketState.value is WebSocketState.Connected) {
                        val isSocketAlive = webSocket?.let { ws ->
                            try {
                                ws.send("ping")
                                true
                            } catch (e: Exception) {
                                false
                            }
                        } ?: false

                        if (isSocketAlive) {
                            Log.d("WebSocket", "Socket is alive, skipping reconnect")
                            isConnecting.set(false)
                            return@withLock
                        }
                    }

                    val newBaseUrl = networkManager.getBaseUrl()

                    // Get current and new URLs in normalized form for comparison
                    val currentBaseUrl = currentWsUrl?.let { url ->
                        when {
                            url.startsWith("wss://") -> "https://" + url.substringAfter("wss://")
                                .substringBefore("/")

                            url.startsWith("ws://") -> "http://" + url.substringAfter("ws://")
                                .substringBefore("/")

                            else -> url.substringBefore("/")
                        }
                    }

                    if (currentBaseUrl == newBaseUrl && _webSocketState.value is WebSocketState.Connected) {
                        Log.d("WebSocket", "Already connected to correct URL, skipping reconnect")
                        isConnecting.set(false)
                        return@withLock
                    }

                    // Close existing connection properly
                    webSocket?.let { ws ->
                        try {
                            ws.close(1000, "Switching connection")
                            delay(100)  // Small delay for clean closure
                        } catch (e: Exception) {
                            Log.e("WebSocket", "Error closing websocket", e)
                        }
                    }
                    webSocket = null

                    setupWebSocket()

                } catch (e: Exception) {
                    Log.e("WebSocket", "Error updating connection", e)
                    _webSocketState.value =
                        WebSocketState.Error("Connection update failed: ${e.message}")
                } finally {
                    isConnecting.set(false)
                }
            }
        }
    }
}

