package com.prodacc.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkStateMonitor(
    private val context: Context,
    private val connectionManager: ConnectionManager
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startMonitoring() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                monitorScope.launch {
                    val newNetworkType = when {
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                            NetworkCapabilities.TRANSPORT_WIFI

                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                            NetworkCapabilities.TRANSPORT_CELLULAR

                        else -> null
                    }

                    // Notify ConnectionManager of network change
                    connectionManager.onNetworkChanged(newNetworkType)

                    // Update connections if needed
                    updateConnections()
                }
            }

            override fun onLost(network: Network) {
                monitorScope.launch {
                    connectionManager.onNetworkChanged(null)
                    delay(1000)
                    updateConnections()
                }
            }
        }

        registerNetworkCallback(networkCallback)
    }

    private fun updateConnections() {
        ApiInstance.updateConnection(context)
        WebSocketInstance.updateConnection()
    }

    private fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }

    fun cleanup() {
        monitorScope.cancel()
    }
}