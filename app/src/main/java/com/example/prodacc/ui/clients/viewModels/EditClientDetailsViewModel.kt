package com.example.prodacc.ui.clients.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModel
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EditClientDetailsViewModel(
    private val clientRepository: ClientRepository = ClientRepository(),
    private val clientId: String
):ViewModel() {


    // Mutable state for client details
    private val _client = MutableStateFlow<Client?>(null)
    val client = _client.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()



    // Dropdown gender toggle state
    val dropGenderToggle = mutableStateOf(false)



    init {
        _loadingState.value = LoadingState.Loading
        loadClientDetails()
    }

    // Load client details when ViewModel is initialized
    private fun loadClientDetails() {
        viewModelScope.launch {
            when (val result = clientRepository.getClientsById((UUID.fromString(clientId)))) {
                is ClientRepository.LoadingResult.SingleEntity -> {
                    result.client?.let {
                        _client.value = it
                        _loadingState.value = LoadingState.Success
                    } ?: run {
                        _loadingState.value = LoadingState.Error("Client Not Found")
                    }
                }
                is ClientRepository.LoadingResult.Error -> {
                    _loadingState.value = LoadingState.Error(result.message ?: "Failed to load client details")
                }
                is ClientRepository.LoadingResult.NetworkError -> {
                    _loadingState.value = LoadingState.Error("Network error. Please check your connection.")
                }
                else -> {}
            }
        }
    }


   fun refreshClient(){
       _loadingState.value = LoadingState.Loading
       _client.value = null
       viewModelScope.launch {
           loadClientDetails()
       }
   }

    // Update methods for client details
    fun updateFirstName(newName: String) {
        _client.value = _client.value?.copy(clientName = newName)
    }

    fun updateSurname(newSurname: String) {
        _client.value = _client.value?.copy(clientSurname = newSurname)
    }

    fun updatePhone(newPhone: String) {
        _client.value = _client.value?.copy(phone = newPhone)
    }

    fun updateEmail(newEmail: String) {
        _client.value = _client.value?.copy(email = newEmail)
    }

    fun updateAddress(newAddress: String) {
        _client.value = _client.value?.copy(address = newAddress)
    }

    fun updateJobTitle(newJobTitle: String) {
        _client.value = _client.value?.copy(jobTitle = newJobTitle)
    }

    fun updateCompany(newCompany: String) {
        _client.value = _client.value?.copy(company = newCompany)
    }

    // Gender-related methods
    fun toggleGender() {
        dropGenderToggle.value = !dropGenderToggle.value
    }

    fun updateGender(newGender: String) {
        _client.value = _client.value?.copy(gender = newGender)
        dropGenderToggle.value = false
    }

    // Save client details
    fun saveClientDetails() {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                val clientId = UUID.fromString(clientId)
                val result = _client.value?.let { clientRepository.updateClient(clientId, it) }
                when (result) {
                    is ClientRepository.LoadingResult.SingleEntity -> {
                        result.client?.let {
                            _client.value = it
                            refreshClient()
                            WebSocketInstance.sendWebSocketMessage("UPDATE_CLIENT", it.id)
                            _loadingState.value = LoadingState.Success
                        } ?: run {
                            _loadingState.value = LoadingState.Error("Failed to update client")
                        }
                    }
                    is ClientRepository.LoadingResult.Error -> {
                        _loadingState.value = LoadingState.Error(result.message ?: "Failed to update client")
                    }
                    is ClientRepository.LoadingResult.NetworkError -> {
                        _loadingState.value = LoadingState.Error("Network error. Please check your connection.")
                    }
                    else -> _loadingState.value = LoadingState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }



    sealed class LoadingState{
        data object Idle : LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message : String): LoadingState()
    }

}

class EditClientDetailsViewModelFactory(private val clientId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(EditClientDetailsViewModel::class.java)) {
            return EditClientDetailsViewModel(clientId = clientId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}