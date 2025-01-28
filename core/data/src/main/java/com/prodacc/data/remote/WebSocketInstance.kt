package com.prodacc.data.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.prodacc.data.NotificationManager
import com.prodacc.data.remote.ApiInstance.BASE_URL
import com.prodacc.data.repositories.JobCardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object WebSocketInstance {
    private val logger = Logger.getLogger(WebSocketInstance::class.java.name)
    private val _webSocketState = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected(""))
    val webSocketState = _webSocketState.asStateFlow()
    private lateinit var applicationContext: Context

    fun initialize(context: Context){
        applicationContext = context.applicationContext
        NotificationManager.createNotificationChannel(applicationContext)
    }

    private val _websocketIndicator = MutableStateFlow(true)
    val webSocketIndicator = _websocketIndicator

    fun websocketIndicatorToggle(state : Boolean){
        _websocketIndicator.value = state
    }

    //WebSocket
    private var webSocket: WebSocket? = null
    private val webSocketListeners = mutableListOf<WebSocketEventListener>()

    // Setting up WebSocket Connection
    private fun setupWebSocket() {
        val token = TokenManager.getToken()?.accessToken ?: run {
            Log.e("WebSocket", "No token available")
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

        Log.d("WebSocket", "Attempting connection to: $wsUrl")


        val client = OkHttpClient.Builder()
            .pingInterval(60, TimeUnit.SECONDS) // Keep connection alive
            .readTimeout(0, TimeUnit.MILLISECONDS) // Important for WebSocket
            .writeTimeout(0, TimeUnit.MILLISECONDS) // Important for WebSocket
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Origin", BASE_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

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
                    showNotificationForType(type, uuid)

                    logger.info("WebSocket Received: $type")

                    when (type) {
                        "NEW_JOB_CARD" -> {
                            logger.info("WebSocket Received: $type")
                            val jobCardId = UUID.fromString(jsonObject.getString("data"))
                            val update = WebSocketUpdate.JobCardCreated(jobCardId)
                            webSocketListeners.forEach { listener ->
                                listener.onWebSocketUpdate(update)
                            }
                        }
                        "DELETE_JOB_CARD" -> {
                            logger.info("WebSocket Received: $type")
                            val jobCardId = UUID.fromString(jsonObject.getString("data"))  // Parse data as string
                            val update = WebSocketUpdate.JobCardDeleted(jobCardId)  // or create a new DeletedJobCard update type
                            webSocketListeners.forEach { listener ->
                                listener.onWebSocketUpdate(update)
                            }
                        }
                        "UPDATE_JOB_CARD" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.JobCardUpdated(id)) }
                        }
                        "JOB_CARD_STATUS_CHANGED" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.StatusChanged(id)) }
                        }
                        "NEW_JOB_CARD_REPORT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewReport(id)) }
                        }
                        "UPDATE_JOB_CARD_REPORT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateReport(id)) }
                        }
                        "DELETE_JOB_CARD_REPORT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteReport(id)) }
                        }
                        "NEW_JOB_CARD_TECHNICIAN" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewTechnician(id)) }
                        }
                        "DELETE_JOB_CARD_TECHNICIAN" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteTechnician(id)) }
                        }
                        "NEW_SERVICE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewServiceChecklist(id)) }
                        }
                        "UPDATE_SERVICE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateServiceChecklist(id)) }
                        }
                        "DELETE_SERVICE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteServiceChecklist(id)) }
                        }
                        "NEW_STATE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewStateChecklist(id)) }
                        }
                        "UPDATE_STATE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateStateChecklist(id)) }
                        }
                        "DELETE_STATE_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteStateChecklist(id)) }
                        }
                        "NEW_CONTROL_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewControlChecklist(id)) }
                        }
                        "UPDATE_CONTROL_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateControlChecklist(id)) }
                        }
                        "DELETE_CONTROL_CHECKLIST" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteControlChecklist(id)) }
                        }
                        "NEW_TIMESHEET" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewTimesheet(id)) }
                        }
                        "UPDATE_TIMESHEET" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateTimesheet(id)) }
                        }
                        "DELETE_TIMESHEET" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteTimesheet(id)) }
                        }
                        "NEW_CLIENT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewClient(id)) }
                        }
                        "UPDATE_CLIENT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateClient(id)) }
                        }
                        "DELETE_CLIENT" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteClient(id)) }
                        }
                        "NEW_VEHICLE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewVehicle(id)) }
                        }
                        "UPDATE_VEHICLE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateVehicle(id)) }
                        }
                        "DELETE_VEHICLE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteVehicle(id)) }
                        }
                        "NEW_EMPLOYEE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.NewEmployee(id)) }
                        }
                        "UPDATE_EMPLOYEE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.UpdateEmployee(id)) }
                        }
                        "DELETE_EMPLOYEE" -> {
                            val id = UUID.fromString(jsonObject.getString("data"))
                            webSocketListeners.forEach { it.onWebSocketUpdate(WebSocketUpdate.DeleteEmployee(id)) }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message", e)
                }


            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _webSocketState.value = WebSocketState.Error("Error: ${t.message}")
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
                _webSocketState.value = WebSocketState.Disconnected("Closed: $reason")
                Log.d("WebSocket", "WebSocket closed: $reason")
            }
        })
    }

    // Connecting to WebSocket
    fun reconnectWebSocket() {
        _webSocketState.value = WebSocketState.Reconnecting("Reconnecting")
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
        webSocket?.close(1000, "App closing")
        webSocket = null
        webSocketListeners.clear()
    }


    sealed class WebSocketState {
        data class Connected(val message: String) : WebSocketState()
        data class Disconnected(val message: String) : WebSocketState()
        data class Reconnecting(val message: String) : WebSocketState()
        data class Error(val message: String) : WebSocketState()
    }

    private fun showNotificationForType(type: String, id: UUID) {
        val (title, message) = when (type) {
            "NEW_JOB_CARD" -> Pair(
                "New Job Card",
                "Job Card #$id has been created"
            )
            "DELETE_JOB_CARD" -> Pair(
                "Job Card Deleted",
                "Job Card #$id has been deleted"
            )
            "JOB_CARD_STATUS_CHANGED" -> Pair(
                "Status Update",
                "Job Card #$id status has been updated"
            )
            "NEW_JOB_CARD_TECHNICIAN" -> Pair(
                "New Assignment",
                "You have been assigned to Job Card #$id"
            )
            "DELETE_JOB_CARD_TECHNICIAN" -> Pair(
                "Assignment Removed",
                "You have been removed from Job Card #$id"
            )
            "NEW_TIMESHEET" -> Pair(
                "New Timesheet",
                "A new timesheet has been added to Job Card #$id"
            )
            "UPDATE_TIMESHEET" -> Pair(
                "Timesheet Updated",
                "A timesheet has been updated on Job Card #$id"
            )
            "NEW_SERVICE_CHECKLIST" -> Pair(
                "New Service Checklist",
                "A service checklist has been created for Job Card #$id"
            )
            "UPDATE_SERVICE_CHECKLIST" -> Pair(
                "Service Checklist Updated",
                "Service checklist has been updated for Job Card #$id"
            )
            "NEW_STATE_CHECKLIST" -> Pair(
                "New State Checklist",
                "A state checklist has been created for Job Card #$id"
            )
            "UPDATE_STATE_CHECKLIST" -> Pair(
                "State Checklist Updated",
                "State checklist has been updated for Job Card #$id"
            )
            "NEW_CONTROL_CHECKLIST" -> Pair(
                "New Control Checklist",
                "A control checklist has been created for Job Card #$id"
            )
            "UPDATE_CONTROL_CHECKLIST" -> Pair(
                "Control Checklist Updated",
                "Control checklist has been updated for Job Card #$id"
            )
            else -> return // No notification for other types
        }

        NotificationManager.showNotification(
            context = applicationContext,
            title = title,
            message = message,
            type = type,
            entityId = id
        )
    }

}

