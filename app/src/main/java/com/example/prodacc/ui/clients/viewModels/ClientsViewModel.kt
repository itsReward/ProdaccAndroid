package com.example.prodacc.ui.clients.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    webSocketInstance: WebSocketInstance,
    signedInUserManager: SignedInUserManager,
    private val tokenManager: TokenManager,
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients = _clients.asStateFlow()

    val userRole = signedInUserManager.role

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)

        viewModelScope.launch {
            EventBus.clientEvent.collect{ event ->
                when(event){
                    EventBus.ClientEvent.ClientCreated -> refreshClients()
                    EventBus.ClientEvent.ClientDeleted -> refreshClients()
                }
            }
        }

        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            fetchClients()
        }
    }

    fun logOut(){
        tokenManager.saveToken(null)
    }

    fun resetLoadState(){
        _loadState.value = LoadState.Idle
    }

    fun refreshClients(){
        _loadState.value = LoadState.Loading
        _clients.value = emptyList()
        viewModelScope.launch {
            fetchClients()
        }
    }

    private suspend fun fetchClients() {
        try {
            when (val clients = clientRepository.getClients()) {
                is ClientRepository.LoadingResult.Success -> {
                    _loadState.value = LoadState.Success
                    _clients.value = clients.clients
                }

                is ClientRepository.LoadingResult.Error -> {
                    _loadState.value = LoadState.Error(clients.message ?: "Unknown Error")
                }

                is ClientRepository.LoadingResult.ErrorSingleMessage -> {
                    _loadState.value = LoadState.Error(clients.message)
                }

                ClientRepository.LoadingResult.NetworkError -> {
                    _loadState.value = LoadState.Error("Network Error")
                }

                is ClientRepository.LoadingResult.SingleEntity -> {
                    //will never happen
                    _loadState.value = LoadState.Error("returned a single entity instead of a list")
                }
            }
        } catch (e: Exception) {
            _loadState.value = LoadState.Error(e.message ?: "Unknown Error")
        }

    }

    sealed class LoadState {
        data object Idle : LoadState()
        data object Loading : LoadState()
        data object Success : LoadState()
        data class Error(val message: String) : LoadState()

    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.NewClient -> {
                viewModelScope.launch {
                    fetchClients()
                }
            }
            is WebSocketUpdate.UpdateClient -> {
                viewModelScope.launch {
                    fetchClients()
                }
            }
            is WebSocketUpdate.DeleteClient -> {
                viewModelScope.launch {
                    fetchClients()
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {
    }

}


