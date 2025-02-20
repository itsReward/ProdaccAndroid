package com.example.prodacc.ui.clients.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.clients.stateClasses.AddClientState
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.NewClient
import com.prodacc.data.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val webSocketInstance: WebSocketInstance
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddClientState())
    val uiState = _uiState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    val genderOptions = listOf("Male", "Female", "Other")

    val genderDropdown = mutableStateOf(false)

    fun onGenderToggle() {
        genderDropdown.value = !genderDropdown.value
    }

    private fun updateUiState(update: AddClientState.() -> AddClientState) {
        _uiState.value = _uiState.value.update()
    }

    fun updateFirstName(firstName: String) {
        updateUiState { copy(firstName = firstName) }
    }

    fun updateSurname(surname: String) {
        updateUiState { copy(secondName = surname) }
    }

    fun updateGender(gender: String) {
        updateUiState { copy(gender = gender) }
        onGenderToggle()
    }

    fun updatePhone(phone: String) {
        updateUiState { copy(phoneNumber = phone) }
    }

    fun updateEmail(email: String) {
        updateUiState { copy(email = email) }
    }

    fun updateAddress(address: String) {
        updateUiState { copy(address = address) }
    }

    fun updateJobTitle(jobTitle: String) {
        updateUiState { copy(jobTitle = jobTitle) }
    }

    fun updateCompany(company: String) {
        updateUiState { copy(company = company) }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    fun saveClient() {
        val state = _uiState.value
        if (state.firstName.isBlank() || state.secondName.isBlank() || state.gender.isBlank() || state.phoneNumber.isBlank() || state.email.isBlank()) {
            _saveState.value = SaveState.Error("Fill all required details")
            return
        }

        viewModelScope.launch {
            try {
                _saveState.value = SaveState.Loading
                when (val result = clientRepository.createClient(state.toNewClient())) {
                    is ClientRepository.LoadingResult.Error -> {
                        _saveState.value = SaveState.Error(result.message ?: "Error")
                    }

                    is ClientRepository.LoadingResult.ErrorSingleMessage -> {
                        _saveState.value = SaveState.Error(result.message)
                    }

                    ClientRepository.LoadingResult.NetworkError -> {
                        _saveState.value = SaveState.Error("Network Error")
                    }

                    is ClientRepository.LoadingResult.SingleEntity -> {
                        EventBus.emitClientEvent(EventBus.ClientEvent.ClientCreated)
                        if (result.client == null) {
                            _saveState.value = SaveState.Error("Returned Null Value")
                        } else {
                            webSocketInstance.sendWebSocketMessage("NEW_CLIENT", result.client!!.id)
                            _saveState.value = SaveState.Success(result.client!!)
                        }

                    }

                    is ClientRepository.LoadingResult.Success -> {
                        // Will never happen for single entity creation
                    }
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Error saving client")
            }
        }
    }

    private fun AddClientState.toNewClient(): NewClient {
        return NewClient(
            clientName = this.firstName,
            clientSurname = this.secondName,
            gender = this.gender,
            phone = this.phoneNumber,
            email = this.email,
            address = this.address,
            jobTitle = this.jobTitle,
            company = this.company
        )
    }

    sealed class SaveState {
        data class Success(val client: Client) : SaveState()
        data class Error(val message: String) : SaveState()
        data object Idle : SaveState()
        data object Loading : SaveState()
    }
}