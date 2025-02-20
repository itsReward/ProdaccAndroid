package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.ControlChecklist
import com.prodacc.data.remote.dao.NewControlChecklist
import com.prodacc.data.repositories.ControlChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ControlChecklistViewModel @Inject constructor(
    private val controlChecklistRepository: ControlChecklistRepository,
    signedInUserManager: SignedInUserManager,
    webSocketInstance: WebSocketInstance,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get jobCardId from SavedStateHandle
    private val jobCardId: String = checkNotNull(savedStateHandle["jobCardId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    val currentUserRole = signedInUserManager.role
    val currentSignedInEmployee = signedInUserManager.employee

    private val _controlChecklist = MutableStateFlow<ControlChecklist?>(null)
    val controlChecklist = _controlChecklist.asStateFlow()

    private val _loadingState =
        MutableStateFlow<ControlChecklistLoadingState>(ControlChecklistLoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _savingState = MutableStateFlow<SaveState>(SaveState.Idle)
    val savingState = _savingState.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)
        viewModelScope.launch {
            fetchControlChecklist()
        }
    }


    private suspend fun fetchControlChecklist() {
        _loadingState.value = ControlChecklistLoadingState.Loading
        try {
            when (val response =
                controlChecklistRepository.getControlChecklist(UUID.fromString(jobCardId))) {
                is ControlChecklistRepository.LoadingResult.Error -> _loadingState.value =
                    ControlChecklistLoadingState.Error(response.message)

                is ControlChecklistRepository.LoadingResult.Loading -> _loadingState.value =
                    ControlChecklistLoadingState.Loading

                is ControlChecklistRepository.LoadingResult.Success -> {
                    _controlChecklist.value = response.data
                    _loadingState.value = ControlChecklistLoadingState.Success
                }
            }

        } catch (e: Exception) {
            when (e) {
                is IOException -> _loadingState.value =
                    ControlChecklistLoadingState.Error("Network Error")

                else -> _loadingState.value =
                    ControlChecklistLoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun saveControlChecklist(checklistData: Map<String, String>) {
        viewModelScope.launch {
            _savingState.value = SaveState.Saving
            try {
                val checklist = NewControlChecklist(
                    jobCardId = UUID.fromString(jobCardId),
                    technicianId = currentSignedInEmployee.value!!.id,
                    created = LocalDateTime.now(),
                    checklist = checklistData
                )

                val result = if (_controlChecklist.value == null) {
                    println("adding new control checklist: ${_controlChecklist.value} must be null")
                    controlChecklistRepository.addControlChecklist(checklist)
                } else {
                    controlChecklistRepository.updateControlChecklist(
                        _controlChecklist.value!!.id,
                        checklist
                    )
                }

                _savingState.value = when (result) {
                    is ControlChecklistRepository.LoadingResult.Error -> SaveState.Error(result.message)
                    is ControlChecklistRepository.LoadingResult.Loading -> SaveState.Saving
                    is ControlChecklistRepository.LoadingResult.Success -> SaveState.Success
                }
                when (_savingState.value) {
                    is SaveState.Success -> fetchControlChecklist()
                    else -> {}
                }

            } catch (e: Exception) {
                _savingState.value = SaveState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refreshControlChecklist() {
        _loadingState.value = ControlChecklistLoadingState.Loading
        _controlChecklist.value = null
        viewModelScope.launch {
            fetchControlChecklist()
        }
    }

    fun resetSaveState() {
        _savingState.value = SaveState.Idle
    }

    sealed class SaveState {
        data object Idle : SaveState()
        data object Saving : SaveState()
        data object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }


    open class ControlChecklistLoadingState {
        data object Idle : ControlChecklistLoadingState()
        data object Loading : ControlChecklistLoadingState()
        data object Success : ControlChecklistLoadingState()
        data class Error(val message: String) : ControlChecklistLoadingState()

    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when (update) {
            is WebSocketUpdate.NewControlChecklist -> {
                if (update.id == UUID.fromString(jobCardId)) {
                    refreshControlChecklist()
                }
            }

            is WebSocketUpdate.UpdateControlChecklist -> {
                if (update.id == UUID.fromString(jobCardId)) {
                    refreshControlChecklist()
                }
            }

            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        //nothing to do here
    }

}
