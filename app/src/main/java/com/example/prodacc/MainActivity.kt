package com.example.prodacc

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.designsystem.theme.ProdaccTheme
import com.example.prodacc.navigation.AppNavigation
import com.prodacc.data.NotificationManager
import com.prodacc.data.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity(){

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission is granted
            Log.d("Permissions", "Notification permission granted")
            // Initialize your notification channel here if needed
        } else {
            // Show settings dialog if permission is denied
            if (!shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // User clicked "Don't ask again", show settings dialog
                showSettingsDialog()
            } else {
                // User just denied once, show rationale
                showPermissionRationaleDialog()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle notification click
        intent?.let { handleNotificationClick(it) }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        NotificationService.startService(this)

        // Request notification permission when activity starts
        requestNotificationPermission()
        setContent {
            ProdaccTheme {

                AppNavigation()

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Required")
            .setMessage("To receive important updates about job cards and assignments, please allow notifications for this app.")
            .setPositiveButton("Allow") { _, _ ->
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Notifications are disabled. Please enable them in settings to receive important updates.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                }
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    // Show in-app rationale about why notification is needed
                    showPermissionRationaleDialog()
                }
                else -> {
                    // Request permission directly
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }


    private fun handleNotificationClick(intent: Intent) {
        val notificationType = intent.getStringExtra("notification_type")
        val entityId = intent.getStringExtra("entity_id")?.let { UUID.fromString(it) }


        if (notificationType != null && entityId != null) {
            // Navigate to appropriate screen based on notification type
            when (notificationType) {
                "NEW_JOB_CARD", "UPDATE_JOB_CARD" -> {
                    // Navigate to JobCard
                }
                "NEW_TIMESHEET" -> {
                    // Navigate to timesheet
                }
                // Add other cases as needed
            }
        }
    }

}



