package com.prodacc.data

import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.prodacc.data.remote.WebSocketInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationService : Service() {
    private val serviceNotificationId = 1
    private val serviceChannelId = "foreground_service_channel"
    private val stopServiceAction = "com.example.prodacc.STOP_SERVICE"
    private var serviceJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        NotificationManager.createNotificationChannel(this)
        startForeground(serviceNotificationId, createServiceNotification())
        WebSocketInstance.initialize(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "Service started")
        when (intent?.action) {
            stopServiceAction -> stopSelf()
            else -> {
                // Schedule service stop after 1 hour
                serviceJob = serviceScope.launch {
                    delay(TimeUnit.HOURS.toMillis(1))
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {

        Log.d("NotificationService", "Service destroyed")
        super.onDestroy()
        serviceJob?.cancel()
        serviceScope.cancel()
        WebSocketInstance.cleanup()
    }

    private fun createServiceNotification(): Notification {
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, NotificationService::class.java).apply { action = stopServiceAction },
            PendingIntent.FLAG_IMMUTABLE
        )

        //Create vibration patter
        val vibrationPattern = longArrayOf(0, 500, 200, 500)

        //get default notification sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(this, serviceChannelId)
            .setContentTitle("Workshop App")
            .setContentText("Staying connected for updates")
            .setSmallIcon(R.drawable.jobkeep_round_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .addAction(0, "Stop", stopIntent)
            .build()
    }

    companion object {
        fun startService(context: Context) {
            context.startForegroundService(Intent(context, NotificationService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, NotificationService::class.java))
        }
    }
}