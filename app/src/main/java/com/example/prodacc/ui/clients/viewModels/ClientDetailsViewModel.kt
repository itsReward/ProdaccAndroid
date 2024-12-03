package com.example.prodacc.ui.clients.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class ClientDetailsViewModel(
    private val clientRepository: ClientRepository = ClientRepository(),
    private val clientId: String
): ViewModel() {
    private val _client = MutableStateFlow<Client?>(null)
    val client = _client.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    private val _deleteClientConfirmation = MutableStateFlow(false)
    val deleteClientConfirmation = _deleteClientConfirmation.asStateFlow()

    init {
        viewModelScope.launch {
            fetchClient()
        }
    }

    fun refreshClient(){
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


    suspend fun fetchClient(){
        try {
            _loadState.value = LoadState.Loading
            val id = UUID.fromString(clientId)
            if (id is UUID){
                val response = clientRepository.getClientsById(id)
                when (response){
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
                val result = clientRepository.deleteClient(clientId)
                when (result) {
                    is ClientRepository.LoadingResult.Success -> {
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
}