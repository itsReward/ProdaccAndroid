package com.prodacc.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.util.logging.Logger

class ConnectionManager(private val context: Context) {
    private val localServerIp = "192.168.100.10"  // Your server's local IP
    private val remoteServer = "api.silverstarzw.com"         // OpenVPN server IP
    private val port = "80"                      // NGINX port
    private val logger = Logger.getLogger(ConnectionManager::class.java.name)

    suspend fun getBaseUrl(): String = withContext(Dispatchers.IO) {
        return@withContext if (isLocalNetworkAvailable()) {
            logger.info("Local network is available. Using local server IP.")
            "http://$localServerIp:$port"
        } else {
            logger.info("Local network is not available. Using remote address.")
            "https://$remoteServer"
        }
    }

    private suspend fun isLocalNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            val timeoutMs = 3000
            socket.connect(InetSocketAddress(localServerIp, port.toInt()), timeoutMs)
            socket.close()
            logger.info("Successfully connected to local server.")
            true
        } catch (e: Exception) {
            logger.severe("Failed to connect to local server: ${e.message}")
            false
        }
    }
}