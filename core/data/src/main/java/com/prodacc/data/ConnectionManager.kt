package com.prodacc.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.util.logging.Logger

class ConnectionManager(private val context: Context) {
    private val localServerIp = "192.168.100.10"
    private val remoteServer = "api.silverstarzw.com"
    private val port = "80"
    private val logger = Logger.getLogger(ConnectionManager::class.java.name)

    private var currentBaseUrl: String? = null
    private var currentNetworkType: Int? = null

    suspend fun getBaseUrl(): String = withContext(Dispatchers.IO) {
        // If we already have a URL and network type hasn't changed, return cached URL
        currentBaseUrl?.let { url ->
            return@withContext url
        }

        // If no cached URL, determine the appropriate URL
        val newUrl = if (isLocalNetworkAvailable()) {
            "http://$localServerIp:$port"
        } else {
            "https://$remoteServer"
        }

        currentBaseUrl = newUrl
        return@withContext newUrl
    }

    private suspend fun isLocalNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.soTimeout = 1000
            socket.connect(InetSocketAddress(localServerIp, port.toInt()), 1000)
            socket.close()
            logger.info("Successfully connected to local server.")
            true
        } catch (e: Exception) {
            logger.info("Failed to connect to local server: ${e.message}")
            false
        }
    }

    // Called by NetworkStateMonitor when network changes
    fun onNetworkChanged(networkType: Int?) {
        if (currentNetworkType != networkType) {
            currentNetworkType = networkType
            currentBaseUrl = null // Force new URL check
            logger.info("Network changed, clearing cached URL")
        }
    }
}