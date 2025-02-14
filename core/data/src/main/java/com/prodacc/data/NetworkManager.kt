package com.prodacc.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket
import java.util.logging.Logger

class NetworkManager private constructor(private val context: Context) {
    // Use WeakReference for context
    private val contextRef = WeakReference(context.applicationContext)
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val logger = Logger.getLogger(NetworkManager::class.java.name)

    private val localServerIp = "10.78.238.123"
    private val remoteServer = "api.silverstarzw.com"
    private val port = "5000"

    // Network state
    private var currentBaseUrl: String? = null
    private var currentNetworkType: Int? = null
    private var isLocalServerAvailable = false

    // StateFlow for network status
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    val networkState = _networkState.asStateFlow()


    // Add NetworkChangeListener interface
    interface NetworkChangeListener {
        fun onNetworkChanged()
    }

    // Use WeakReferences for listeners
    private val networkChangeListeners = mutableListOf<WeakReference<NetworkChangeListener>>()

    fun addNetworkChangeListener(listener: NetworkChangeListener) {
        networkChangeListeners.add(WeakReference(listener))
    }

    fun removeNetworkChangeListener(listener: NetworkChangeListener) {
        networkChangeListeners.removeAll { it.get() == listener }
    }



    fun startMonitoring() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                monitorScope.launch {
                    handleNetworkChange(networkCapabilities)
                }
            }

            override fun onLost(network: Network) {
                monitorScope.launch {
                    _networkState.value = NetworkState.Disconnected
                    currentNetworkType = null
                    currentBaseUrl = null
                    isLocalServerAvailable = false
                    notifyNetworkChange()
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private suspend fun handleNetworkChange(networkCapabilities: NetworkCapabilities) {
        val newNetworkType = when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                NetworkCapabilities.TRANSPORT_WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                NetworkCapabilities.TRANSPORT_CELLULAR
            else -> null
        }

        // Only process if network type actually changed
        if (newNetworkType != currentNetworkType) {
            currentNetworkType = newNetworkType
            currentBaseUrl = null // Force URL check on network change

            when (newNetworkType) {
                NetworkCapabilities.TRANSPORT_WIFI -> {
                    checkLocalServerAvailability()
                }
                NetworkCapabilities.TRANSPORT_CELLULAR -> {
                    isLocalServerAvailable = false
                    _networkState.value = NetworkState.Connected(NetworkType.CELLULAR)
                    notifyNetworkChange()
                }
                else -> {
                    _networkState.value = NetworkState.Disconnected
                    notifyNetworkChange()
                }
            }
        }
    }

    private suspend fun checkLocalServerAvailability() {
        try {

            val socket = Socket()
            socket.soTimeout = 1000
            withContext(Dispatchers.IO) {
                socket.connect(InetSocketAddress(localServerIp, port.toInt()), 1000)
                socket.close()
            }
            isLocalServerAvailable = true
            _networkState.value = NetworkState.Connected(NetworkType.LOCAL_WIFI)
        } catch (e: Exception) {
            isLocalServerAvailable = false
            _networkState.value = NetworkState.Connected(NetworkType.REMOTE_WIFI)
            logger.info("Local server not available: ${e.message}")
        } finally {
            notifyNetworkChange()
        }
    }

    suspend fun getBaseUrl(): String = withContext(Dispatchers.IO) {
        currentBaseUrl?.let { return@withContext it }

        val newUrl = when (_networkState.value) {
            is NetworkState.Connected -> {
                when ((_networkState.value as NetworkState.Connected).type) {
                    NetworkType.LOCAL_WIFI -> "http://$localServerIp:$port"
                    else -> "http://$localServerIp:$port"  //REMOVE LOCALSERVER IP AND REPLACE WITH REMOTE SERVER
                }
            }
            NetworkState.Disconnected -> "http://$localServerIp:$port" //REMOVE LOCALSERVER IP AND REPLACE WITH REMOTE SERVER
        }

        currentBaseUrl = newUrl
        newUrl
    }

    private fun notifyNetworkChange() {// Clean up any null references and notify listeners
        networkChangeListeners.removeAll { it.get() == null }
        networkChangeListeners.forEach {
            it.get()?.onNetworkChanged()
        }
    }

    fun cleanup() {
        monitorScope.cancel()
        networkChangeListeners.clear()
        instance = null
    }

    sealed class NetworkState {
        data class Connected(val type: NetworkType) : NetworkState()
        object Disconnected : NetworkState()
    }

    enum class NetworkType {
        LOCAL_WIFI,
        REMOTE_WIFI,
        CELLULAR
    }

    companion object {
        @Volatile
        private var instance: NetworkManager? = null

        fun getInstance(context: Context): NetworkManager {
            return instance ?: synchronized(this) {
                instance ?: NetworkManager(context).also { instance = it }
            }
        }
    }
}

