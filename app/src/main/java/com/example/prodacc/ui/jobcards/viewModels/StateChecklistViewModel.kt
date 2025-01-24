package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.NewStateChecklist
import com.prodacc.data.remote.dao.StateChecklist
import com.prodacc.data.remote.dao.User
import com.prodacc.data.repositories.StateChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class StateChecklistViewModel(
    private val stateChecklistRepository: StateChecklistRepository = StateChecklistRepository(),
    private val jobCardId: String
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _stateChecklist = MutableStateFlow<StateChecklist?>(null)
    val stateChecklist: StateFlow<StateChecklist?> = _stateChecklist

    private val _loadingState =
        MutableStateFlow<StateChecklistLoadingState>(StateChecklistLoadingState.Idle)
    val loadingState: StateFlow<StateChecklistLoadingState> = _loadingState

    private val _savingState = MutableStateFlow<SaveState>(SaveState.Idle)
    val savingState = _savingState.asStateFlow()

    private val _fuelLevelIn = MutableStateFlow(_stateChecklist.value?.fuelLevelIn.let { "Select" })
    val fuelLevelIn = _fuelLevelIn.asStateFlow()

    private val _fuelLevelOut = MutableStateFlow(_stateChecklist.value?.fuelLevelOut.let { "Select" })
    val fuelLevelOut = _fuelLevelOut.asStateFlow()

    private val _millageIn = MutableStateFlow(_stateChecklist.value?.millageIn.let { "" })
    val millageIn = _millageIn.asStateFlow()

    private val _millageOut = MutableStateFlow(_stateChecklist.value?.millageOut.let { "" })
    val millageOut = _millageOut.asStateFlow()


    init {
        WebSocketInstance.addWebSocketListener(this)

        viewModelScope.launch {
            fetchStateChecklist()
        }
    }

    private suspend fun fetchStateChecklist() {
        _loadingState.value = StateChecklistLoadingState.Loading
        try {
            when (val response =
                stateChecklistRepository.getStateChecklist(UUID.fromString(jobCardId))) {
                is StateChecklistRepository.LoadingResults.Error -> _loadingState.value =
                    StateChecklistLoadingState.Error(response.message)

                is StateChecklistRepository.LoadingResults.Loading -> _loadingState.value =
                    StateChecklistLoadingState.Loading

                is StateChecklistRepository.LoadingResults.Success -> {
                    println(response.stateChecklist)
                    if (response.stateChecklist != null){
                        _stateChecklist.value = response.stateChecklist
                        _fuelLevelIn.value = _stateChecklist.value!!.fuelLevelIn
                        _fuelLevelOut.value = _stateChecklist.value!!.fuelLevelOut
                        _millageIn.value = _stateChecklist.value!!.millageIn
                        _millageOut.value = _stateChecklist.value!!.millageOut
                    } else {
                        _stateChecklist.value = null
                    }
                    _loadingState.value = StateChecklistLoadingState.Success
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> _loadingState.value =
                    StateChecklistLoadingState.Error("Network Error")

                else -> _loadingState.value =
                    StateChecklistLoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun updateFuelLevelIn(value: String) {
        _fuelLevelIn.value = value
    }

    fun updateFuelLevelOut(value: String) {
        _fuelLevelOut.value = value
    }

    fun updateMillageIn(value: String) {
        _millageIn.value = value
    }

    fun updateMillageOut(value: String) {
        _millageOut.value = value
    }

    fun saveStateChecklist(checklistData: Map<String, String>) {
        viewModelScope.launch {
            _savingState.value = SaveState.Saving
            try {
                val checklist = NewStateChecklist(
                    jobCardId = UUID.fromString(jobCardId),
                    created = LocalDateTime.now(),
                    checklist = checklistData,
                    fuelLevelIn = _fuelLevelIn.value,
                    fuelLevelOut = _fuelLevelOut.value,
                    millageIn = _millageIn.value,
                    millageOut = millageOut.value,
                    technicianId = SignedInUser.employee!!.id
                )

                val result = if (_stateChecklist.value == null) {
                    println("adding new service checklist: ${_stateChecklist.value} must be null")
                    stateChecklistRepository.newStateChecklist(checklist)
                } else {
                    stateChecklistRepository.updateStateChecklist(
                        _stateChecklist.value!!.id,
                        checklist
                    )
                }

                _savingState.value = when (result) {
                    is StateChecklistRepository.LoadingResults.Error -> SaveState.Error(result.message)
                    is StateChecklistRepository.LoadingResults.Loading -> SaveState.Saving
                    is StateChecklistRepository.LoadingResults.Success -> SaveState.Success
                }
                when (_savingState.value) {
                    is SaveState.Success -> {
                        fetchStateChecklist()
                    }
                    else -> {}
                }

            } catch (e: Exception) {
                _savingState.value = SaveState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetSaveState() {
        _savingState.value = SaveState.Idle
    }

    fun refreshStateChecklist() {
        _loadingState.value = StateChecklistLoadingState.Loading
        _stateChecklist.value = null
        viewModelScope.launch {
            fetchStateChecklist()
        }
    }


    sealed class SaveState {
        data object Idle : SaveState()
        data object Saving : SaveState()
        data object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    sealed class StateChecklistLoadingState {
        data object Idle : StateChecklistLoadingState()
        data object Loading : StateChecklistLoadingState()
        data class Error(val message: String) : StateChecklistLoadingState()
        data object Success : StateChecklistLoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.NewStateChecklist -> {
                if (update.id == UUID.fromString(jobCardId)){
                    refreshStateChecklist()
                }
            }
            is WebSocketUpdate.UpdateStateChecklist -> {
                if (update.id == UUID.fromString(jobCardId)){
                    refreshStateChecklist()
                }
            }
            is WebSocketUpdate.DeleteStateChecklist -> {
                if (update.id == UUID.fromString(jobCardId)){
                    _loadingState.value = StateChecklistLoadingState.Error("State Checklist has been deleted")
                    _stateChecklist.value = null
                }
            }
            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
       //nothing to do here
    }
}

class StateChecklistViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(StateChecklistViewModel::class.java)) {
            return StateChecklistViewModel(jobCardId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}