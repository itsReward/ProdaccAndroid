package com.example.prodacc

import android.app.Application
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebSocketInstance.initialize(this)
        ApiInstance.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        WebSocketInstance.cleanup()

    }
}