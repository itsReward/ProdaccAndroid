package com.prodacc.data

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import com.prodacc.data.repositories.JobCardTechnicianRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jobCardTechnicianRepository: JobCardTechnicianRepository,
    private val signedInUserManager: SignedInUserManager,
)
{

    private var notificationId = 100

    companion object {
        const val WORKSHOP_CHANNEL_ID = "workshop_notifications"
        const val SERVICE_CHANNEL_ID = "foreground_service_channel"
        private const val NOTIFICATION_PERMISSION_CODE = 123
    }

    fun createNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create all channels
        val channels = listOf(
            createWorkshopChannel(),
            createServiceChannel()
        )

        notificationManager.createNotificationChannels(channels)
        Log.d("NotificationManager", "Notification channels created successfully")
    }

    private fun createWorkshopChannel(): NotificationChannel {
        return NotificationChannel(
            WORKSHOP_CHANNEL_ID,
            "Workshop Notifications",
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Workshop system notifications"
            setShowBadge(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
            enableLights(true)
            lightColor = Color.BLUE

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            setAllowBubbles(true)
        }
    }

    private fun createServiceChannel(): NotificationChannel {
        return NotificationChannel(
            SERVICE_CHANNEL_ID,
            "Connection Service",
            android.app.NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps the app connected in background"
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            importance = android.app.NotificationManager.IMPORTANCE_LOW
            setAllowBubbles(false)
        }
    }

    fun showNotification(
        title: String,
        message: String,
        type: String,
        entityId: UUID,
        activity: Activity? = null
    ) {
        if (!checkNotificationPermission(activity)) {
            activity?.let { showNotificationPermissionDialog(it) }
            return
        }

        if (!runBlocking { shouldShowNotification(type, entityId) }) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", type)
            putExtra("entity_id", entityId.toString())
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val person = Person.Builder()
            .setName("Workshop Updates")
            .setImportant(true)
            .build()

        val messagingStyle = NotificationCompat.MessagingStyle(person)
            .addMessage(message, System.currentTimeMillis(), person)

        val notification = NotificationCompat.Builder(context, WORKSHOP_CHANNEL_ID)
            .setSmallIcon(R.drawable.jobkeep_round_icon)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(messagingStyle)
            .setContentTitle(title)
            .setContentText(message)
            .setTimeoutAfter(30000)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        try {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(notificationId++, notification)
            Log.d("NotificationManager", "Notification shown successfully: $title")
        } catch (e: Exception) {
            Log.e("NotificationManager", "Error showing notification: ${e.message}")
            e.printStackTrace()
        }
    }


    fun createServiceNotification(context: Context, stopIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
            .setContentTitle("Workshop App")
            .setContentText("Staying connected for updates")
            .setSmallIcon(R.drawable.jobkeep_round_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, "Stop", stopIntent)
            .build()
    }

    private suspend fun shouldShowNotification(type: String, entityId: UUID): Boolean {
        return when (signedInUserManager.role.value) {
            is SignedInUserManager.Role.Admin -> true // Admin sees everything
            is SignedInUserManager.Role.Supervisor -> shouldShowSupervisorNotification(type, entityId)
            is SignedInUserManager.Role.ServiceAdvisor -> shouldShowServiceAdvisorNotification(type, entityId)
            is SignedInUserManager.Role.Technician -> shouldShowTechnicianNotification(type, entityId)
            null -> false
        }
    }

    private suspend fun shouldShowTechnicianNotification(type: String, entityId: UUID): Boolean {
        // For technicians, only show notifications for their assigned job cards
        val technicianId = signedInUserManager.employee.value!!.id

        return when (type) {
            "NEW_COMMENT", "DELETE_COMMENT",
            "NEW_JOB_CARD_TECHNICIAN",
            "DELETE_JOB_CARD_TECHNICIAN" -> {
                // Check if the technician is assigned to this job card
                val jobCardTechs = jobCardTechnicianRepository.getJobCardTechnicians(entityId)
                when (jobCardTechs) {
                    is JobCardTechnicianRepository.LoadingResult.Success ->
                        jobCardTechs.list.contains(technicianId)
                    else -> false
                }
            }
            else -> false
        }
    }

    private fun shouldShowServiceAdvisorNotification(type: String, entityId: UUID): Boolean {
        return when (type) {
            "NEW_JOB_CARD", "UPDATE_JOB_CARD", "DELETE_JOB_CARD","NEW_COMMENT", "DELETE_COMMENT",
            "JOB_CARD_STATUS_CHANGED", "NEW_TIMESHEET", "UPDATE_TIMESHEET" -> true
            else -> false
        }
    }

    private fun shouldShowSupervisorNotification(type: String, entityId: UUID): Boolean {
        return when (type) {
            "NEW_JOB_CARD", "UPDATE_JOB_CARD", "DELETE_JOB_CARD","NEW_COMMENT", "DELETE_COMMENT",
            "JOB_CARD_STATUS_CHANGED", "NEW_TIMESHEET", "UPDATE_TIMESHEET",
            "NEW_SERVICE_CHECKLIST", "UPDATE_SERVICE_CHECKLIST",
            "NEW_STATE_CHECKLIST", "UPDATE_STATE_CHECKLIST",
            "NEW_CONTROL_CHECKLIST", "UPDATE_CONTROL_CHECKLIST" -> true
            else -> false
        }
    }

    private fun checkNotificationPermission(activity: Activity?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> true

                activity != null -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                    )
                    false
                }

                else -> false
            }
        } else {
            true
        }
    }


    private fun showNotificationPermissionDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Enable Notifications")
            .setMessage("Notifications are required to receive important updates about job cards and assignments. Please enable notifications in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openNotificationSettings(activity)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openNotificationSettings(activity: Activity) {
        val intent = run {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            }
        }
        activity.startActivity(intent)
    }

}

