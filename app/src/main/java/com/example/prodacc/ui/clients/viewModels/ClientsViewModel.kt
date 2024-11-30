package com.example.prodacc.ui.clients.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designsystem.designComponents.ListCategory
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientsViewModel(
    private val clientRepository: ClientRepository = ClientRepository()
) : ViewModel() {
    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients = _clients.asStateFlow()

    val clientsList =
        clients.value.sortedBy { it.clientName.first() }.groupBy { it.clientName.first() }
            .map { ListCategory(name = it.key.toString(), items = it.value) }

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchClients()
        }
    }

    fun resetLoadState(){
        _loadState.value = LoadState.Idle
    }

    fun refreshClients(){
        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            fetchClients()
        }
    }

    suspend fun fetchClients() {
        _loadState.value = LoadState.Loading
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

}


