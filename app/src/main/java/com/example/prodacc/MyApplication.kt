package com.example.prodacc

import android.app.Application
import com.prodacc.data.remote.ApiInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ApiInstance.initialize(applicationContext)
    }
}