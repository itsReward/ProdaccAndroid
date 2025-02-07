package com.prodacc.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance

class NetworkStateMonitor(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun startMonitoring() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                ApiInstance.updateConnection(context)
                WebSocketInstance.updateConnection()
            }

            override fun onLost(network: Network) {
                ApiInstance.updateConnection(context)
                WebSocketInstance.updateConnection()
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}