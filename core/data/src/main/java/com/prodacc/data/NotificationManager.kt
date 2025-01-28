package com.prodacc.data

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import com.prodacc.data.repositories.JobCardTechnicianRepository
import kotlinx.coroutines.runBlocking
import java.util.UUID

object NotificationManager {
    // Create separate channels for different types of notifications
    private const val WORKSHOP_CHANNEL_ID = "workshop_notifications"
    private const val SERVICE_CHANNEL_ID = "foreground_service_channel"
    private var notificationId = 100  // Start workshop notifications from 100

    fun createNotificationChannel(context: Context) {
        // Workshop notifications channel
        val workshopChannel = NotificationChannel(
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

        // Service notification channel
        val serviceChannel = NotificationChannel(
            SERVICE_CHANNEL_ID,
            "Connection Service",
            android.app.NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps the app connected in background"
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannels(listOf(workshopChannel, serviceChannel))

    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        type: String,
        entityId: UUID
    ) {

        // Check permissions first
        if (!checkNotificationPermission(context)) {
            (context as? Activity)?.let { activity ->
                showNotificationPermissionDialog(activity)
            }
            return
        }

        // Check if user should receive this notification based on role
        if (!runBlocking { shouldShowNotification(type, entityId) }) return

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        //Create vibration patter
        val vibrationPattern = longArrayOf(0, 500, 200, 500)

        //get default notification sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, WORKSHOP_CHANNEL_ID)
            .setSmallIcon(R.drawable.jobkeep_round_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setContentIntent(pendingIntent)
            .build()

        try {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(notificationId++, notification)
            Log.d("NotificationManager", "Notification shown successfully: $title")
        } catch (e: Exception){
            Log.e("NotificationManager", "Error showing notification: ${e.message}")
            e.printStackTrace()
        }

    }

    private suspend fun shouldShowNotification(type: String, entityId: UUID): Boolean {
        return when (SignedInUser.role) {
            is SignedInUser.Role.Admin -> true // Admin sees everything
            is SignedInUser.Role.Supervisor -> shouldShowSupervisorNotification(type, entityId)
            is SignedInUser.Role.ServiceAdvisor -> shouldShowServiceAdvisorNotification(type, entityId)
            is SignedInUser.Role.Technician -> shouldShowTechnicianNotification(type, entityId)
            null -> false
        }
    }

    private suspend fun shouldShowTechnicianNotification(type: String, entityId: UUID): Boolean {
        // For technicians, only show notifications for their assigned job cards
        val technicianId = SignedInUser.employee?.id ?: return false

        return when (type) {
            "NEW_JOB_CARD_TECHNICIAN",
            "DELETE_JOB_CARD_TECHNICIAN" -> {
                // Check if the technician is assigned to this job card
                val jobCardTechs = JobCardTechnicianRepository().getJobCardTechnicians(entityId)
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
            "NEW_JOB_CARD", "UPDATE_JOB_CARD", "DELETE_JOB_CARD",
            "JOB_CARD_STATUS_CHANGED", "NEW_TIMESHEET", "UPDATE_TIMESHEET" -> true
            else -> false
        }
    }

    private fun shouldShowSupervisorNotification(type: String, entityId: UUID): Boolean {
        return when (type) {
            "NEW_JOB_CARD", "UPDATE_JOB_CARD", "DELETE_JOB_CARD",
            "JOB_CARD_STATUS_CHANGED", "NEW_TIMESHEET", "UPDATE_TIMESHEET",
            "NEW_SERVICE_CHECKLIST", "UPDATE_SERVICE_CHECKLIST",
            "NEW_STATE_CHECKLIST", "UPDATE_STATE_CHECKLIST",
            "NEW_CONTROL_CHECKLIST", "UPDATE_CONTROL_CHECKLIST" -> true
            else -> false
        }
    }

    private const val NOTIFICATION_PERMISSION_CODE = 123

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> true

                context is Activity -> {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                    )
                    false
                }

                else -> false
            }
        } else {
            true // Permission not required for Android 12 and below
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