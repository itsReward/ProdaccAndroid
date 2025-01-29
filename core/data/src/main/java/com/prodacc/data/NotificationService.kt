package com.prodacc.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class NotificationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isServiceRunning = false
    private val serviceNotificationId = 1
    private val stopServiceAction = "com.example.prodacc.STOP_SERVICE"
    private val serviceRestartAction = "com.example.prodacc.RESTART_SERVICE"

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        NotificationManager.createNotificationChannels(this)
        setupForegroundService()
        setupWebSocket()
    }

    private fun setupForegroundService() {
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, NotificationService::class.java).apply { action = stopServiceAction },
            PendingIntent.FLAG_IMMUTABLE
        )
        startForeground(serviceNotificationId, NotificationManager.createServiceNotification(this, stopIntent))
    }

    private fun setupWebSocket() {
        serviceScope.launch {
            while (isServiceRunning) {
                try {
                    WebSocketInstance.initialize(applicationContext)
                    // Keep checking WebSocket state
                    WebSocketInstance.webSocketState.collect { state ->
                        when (state) {
                            is WebSocketInstance.WebSocketState.Disconnected,
                            is WebSocketInstance.WebSocketState.Error -> {
                                delay(5000)
                                WebSocketInstance.reconnectWebSocket()
                            }
                            else -> { /* Connected or Reconnecting */ }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NotificationService", "WebSocket error", e)
                    delay(5000)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            stopServiceAction -> {
                isServiceRunning = false
                stopSelf()
            }
            serviceRestartAction -> {
                setupWebSocket()
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartIntent = Intent(this, NotificationService::class.java).apply {
            action = serviceRestartAction
        }
        val pendingIntent = PendingIntent.getService(
            this, 1, restartIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            pendingIntent
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        serviceScope.cancel()
        WebSocketInstance.cleanup()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun startService(context: Context) {
            context.startForegroundService(Intent(context, NotificationService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, NotificationService::class.java))
        }
    }

}

