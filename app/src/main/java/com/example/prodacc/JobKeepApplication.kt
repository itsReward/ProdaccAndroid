package com.example.prodacc

import android.app.Application
import com.prodacc.data.NetworkManager
import com.prodacc.data.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JobKeepApplication : Application() {

    @Inject
    lateinit var networkManager: NetworkManager

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        // Initialise network monitoring
        networkManager.startMonitoring()

        //initialize notification channels
        notificationManager.createNotificationChannels(this.applicationContext)

    }

    override fun onTerminate() {
        super.onTerminate()
        networkManager.cleanup()
    }
}