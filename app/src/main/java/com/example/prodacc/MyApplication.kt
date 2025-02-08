package com.example.prodacc

import android.app.Application
import com.prodacc.data.ConnectionManager
import com.prodacc.data.NetworkStateMonitor
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    private lateinit var networkMonitor: NetworkStateMonitor
    private lateinit var connectionManager: ConnectionManager

    override fun onCreate() {

        super.onCreate()

        connectionManager = ConnectionManager(this)

        networkMonitor = NetworkStateMonitor(this, connectionManager).also {
            it.startMonitoring()
        }

        WebSocketInstance.initialize(this)
        ApiInstance.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        WebSocketInstance.cleanup()
        networkMonitor.cleanup()
    }
}