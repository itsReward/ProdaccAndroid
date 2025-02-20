package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.NewServiceChecklist
import com.prodacc.data.remote.dao.ServiceChecklist
import com.prodacc.data.repositories.ServiceChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServiceChecklistViewModel @Inject constructor(
    private val serviceChecklistRepository: ServiceChecklistRepository,
    webSocketInstance: WebSocketInstance,
    signedInUserManager: SignedInUserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get jobCardId from SavedStateHandle
    private val jobCardId: String = checkNotNull(savedStateHandle["jobCardId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    val userRole = signedInUserManager.role
    val signedInEmployee = signedInUserManager.employee

    private val _serviceChecklist = MutableStateFlow<ServiceChecklist?>(null)
    val serviceChecklist = _serviceChecklist.asStateFlow()

    private val _loadingState =
        MutableStateFlow<ServiceChecklistLoadingState>(ServiceChecklistLoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _savingState = MutableStateFlow<SaveState>(SaveState.Idle)
    val savingState = _savingState.asStateFlow()


    init {
        webSocketInstance.addWebSocketListener(this)

        viewModelScope.launch {
            fetchServiceChecklist()
        }
    }


    fun saveServiceChecklist(checklistData: Map<String, String>) {
        viewModelScope.launch {
            _savingState.value = SaveState.Saving
            try {
                val checklist = NewServiceChecklist(
                    jobCardId = UUID.fromString(jobCardId),
                    technicianId = signedInEmployee.value!!.id,
                    created = LocalDateTime.now(),
                    checklist = checklistData
                )

                val result = if (_serviceChecklist.value == null) {
                    println("adding new service checklist: ${_serviceChecklist.value} must be null")
                    serviceChecklistRepository.addServiceChecklist(checklist)
                } else {
                    serviceChecklistRepository.updateServiceChecklist(
                        _serviceChecklist.value!!.id,
                        checklist
                    )
                }

                _savingState.value = when (result) {
                    is ServiceChecklistRepository.LoadingResult.Error -> SaveState.Error(result.message)
                    is ServiceChecklistRepository.LoadingResult.Loading -> SaveState.Saving
                    is ServiceChecklistRepository.LoadingResult.Success -> SaveState.Success
                }
                when (_savingState.value) {
                    is SaveState.Success -> fetchServiceChecklist()
                    else -> {}
                }

            } catch (e: Exception) {
                _savingState.value = SaveState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    private suspend fun fetchServiceChecklist() {
        _loadingState.value = ServiceChecklistLoadingState.Loading
        try {
            when (val response =
                serviceChecklistRepository.getServiceChecklistByJobCard(UUID.fromString(jobCardId))) {
                is ServiceChecklistRepository.LoadingResult.Error -> _loadingState.value =
                    ServiceChecklistLoadingState.Error(response.message)

                is ServiceChecklistRepository.LoadingResult.Loading -> _loadingState.value =
                    ServiceChecklistLoadingState.Loading

                is ServiceChecklistRepository.LoadingResult.Success -> {
                    _serviceChecklist.value = response.data
                    _loadingState.value = ServiceChecklistLoadingState.Success
                }
            }

        } catch (e: Exception) {
            when (e) {
                is IOException -> _loadingState.value =
                    ServiceChecklistLoadingState.Error("Network Error")

                else -> _loadingState.value =
                    ServiceChecklistLoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun resetSaveState() {
        _savingState.value = SaveState.Idle
    }

    fun refreshServiceChecklist() {
        _loadingState.value = ServiceChecklistLoadingState.Loading
        _serviceChecklist.value = null
        viewModelScope.launch {
            fetchServiceChecklist()
        }
    }


    sealed class SaveState {
        data object Idle : SaveState()
        data object Saving : SaveState()
        data object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    sealed class ServiceChecklistLoadingState {
        data object Idle : ServiceChecklistLoadingState()
        data object Loading : ServiceChecklistLoadingState()
        data object Success : ServiceChecklistLoadingState()
        data class Error(val message: String) : ServiceChecklistLoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.NewServiceChecklist -> {
                if (update.id == UUID.fromString(jobCardId)){
                    refreshServiceChecklist()
                }
            }
            is WebSocketUpdate.UpdateServiceChecklist -> {
                if (update.id == UUID.fromString(jobCardId)) {
                    refreshServiceChecklist()
                }
            }
            is WebSocketUpdate.DeleteServiceChecklist -> {
                if (update.id == UUID.fromString(jobCardId)) {
                    _loadingState.value = ServiceChecklistLoadingState.Error("Service Checklist deleted")
                    refreshServiceChecklist()
                }
            }
            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        //to do something here
    }
}
