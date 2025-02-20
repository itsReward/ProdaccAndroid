package com.example.prodacc.ui.clients.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ClientDetailsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val webSocketInstance: WebSocketInstance,
    signedInUserManager: SignedInUserManager,
    savedStateHandle: SavedStateHandle
): ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get clientId from SavedStateHandle
    private val clientId: String = checkNotNull(savedStateHandle["clientId"]) {
        "clientId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    val currentUserRole = signedInUserManager.role

    private val _client = MutableStateFlow<Client?>(null)
    val client = _client.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    private val _deleteClientConfirmation = MutableStateFlow(false)
    val deleteClientConfirmation = _deleteClientConfirmation.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)

        viewModelScope.launch {
            EventBus.vehicleEvent.collect{ event ->
                when (event){
                    EventBus.VehicleEvent.VehicleCreated -> refreshClient()
                    EventBus.VehicleEvent.VehicleDeleted -> refreshClient()
                }
            }
        }

        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            fetchClient()
        }
    }

    fun refreshClient(){
        _loadState.value = LoadState.Loading
        _client.value = null
        viewModelScope.launch {
            fetchClient()
        }
    }

    fun refreshDeleteState(){
        _deleteState.value = DeleteState.Idle
    }

    fun toggleDeleteClientConfirmation(){
        _deleteClientConfirmation.value = !_deleteClientConfirmation.value
    }

    fun resetDeleteClientConfirmation(){
        _deleteClientConfirmation.value = false
    }


    private suspend fun fetchClient(){
        try {
            val id = UUID.fromString(clientId)
            if (id is UUID){
                when (val response = clientRepository.getClientsById(id)){
                    is ClientRepository.LoadingResult.SingleEntity -> {
                        if (response.client != null){
                            _client.value = response.client
                            _loadState.value = LoadState.Success
                        } else {
                            _loadState.value = LoadState.Error(response.error?: "Unknown Error")
                        }
                    }

                    is ClientRepository.LoadingResult.Error -> {
                        _loadState.value = LoadState.Error(response.message?:"Unknown Error")
                    }
                    is ClientRepository.LoadingResult.ErrorSingleMessage -> {
                        _loadState.value = LoadState.Error(response.message)
                    }
                    ClientRepository.LoadingResult.NetworkError -> {
                        _loadState.value = LoadState.Error("Network Error")
                    }
                    is ClientRepository.LoadingResult.Success -> {
                        //will never happen
                        _loadState.value = LoadState.Error("Returned List instead of Client")
                    }
                }

            } else {
                _loadState.value = LoadState.Error("Invalid String Id Argument")
            }
        }catch (e:Exception){
            when (e){
                is IOException -> {
                    _loadState.value = LoadState.Error("Network Error")
                }
                else -> {
                    _loadState.value = LoadState.Error(e.message?:"Unknown Error")
                }
            }
        }
    }

    // Delete client
    fun deleteClient() {
        viewModelScope.launch {
            _deleteClientConfirmation.value = false
            _deleteState.value = DeleteState.Loading
            try {
                val clientId = UUID.fromString(clientId)
                when (val result = clientRepository.deleteClient(clientId)) {
                    is ClientRepository.LoadingResult.Success -> {
                        EventBus.emitClientEvent(EventBus.ClientEvent.ClientDeleted)
                        webSocketInstance.sendWebSocketMessage("DELETE_CLIENT", clientId)
                        _deleteState.value = DeleteState.Success

                    }
                    is ClientRepository.LoadingResult.Error -> {
                        _deleteState.value = DeleteState.Error(result.message ?: "Deletion failed")
                    }
                    is ClientRepository.LoadingResult.NetworkError -> {
                        _deleteState.value = DeleteState.Error("Network error. Please check your connection.")
                    }
                    else -> _deleteState.value = DeleteState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }


    sealed class DeleteState {
        data object Idle : DeleteState()
        data object Loading : DeleteState()
        data object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }
    sealed class LoadState{
        data object Idle: LoadState()
        data object Loading: LoadState()
        data object Success: LoadState()
        data class Error(val message: String): LoadState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.UpdateClient -> {
                if (update.id == UUID.fromString(clientId)){
                    refreshClient()
                }
            }
            is WebSocketUpdate.DeleteClient -> {
                if (update.id == UUID.fromString(clientId)){
                    _loadState.value = LoadState.Error("Client Deleted")
                }
            }
            else -> {}

        }
    }

    override fun onWebSocketError(error: Throwable) {

    }
}

