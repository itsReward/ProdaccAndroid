package com.example.prodacc.ui

import androidx.lifecycle.ViewModel
import com.prodacc.data.remote.WebSocketInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val webSocketInstance: WebSocketInstance
) : ViewModel() {
    val webSocketState = webSocketInstance.webSocketState
    val webSocketIndicator = webSocketInstance.webSocketIndicator

    fun reconnectWebSocket() {
        webSocketInstance.reconnectWebSocket()
    }

    fun websocketIndicatorToggle(state: Boolean) {
        webSocketInstance.websocketIndicatorToggle(state)
    }
}