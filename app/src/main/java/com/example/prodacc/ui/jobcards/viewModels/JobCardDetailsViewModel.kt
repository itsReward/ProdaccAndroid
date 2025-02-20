package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobCardDetailsViewModel @Inject constructor(
    private val jobCardRepository: JobCardRepository,
    private val jobCardStatusRepository: JobCardStatusRepository,
    private val webSocketInstance: WebSocketInstance,
    signedInUserManager: SignedInUserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get jobCardId from SavedStateHandle
    private val jobId: String = checkNotNull(savedStateHandle["jobCardId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    private val _jobCard = MutableStateFlow<JobCard?>(null)
    val jobCard = _jobCard.asStateFlow()

    private val _jobCardStatusList = MutableStateFlow<List<JobCardStatus>>(emptyList())
    val jobCardStatusList = _jobCardStatusList.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _savingState = MutableStateFlow<SaveState>(SaveState.Idle)
    val savingState = _savingState.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _statusLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val statusLoadingState = _statusLoadingState.asStateFlow()

    val userRole = signedInUserManager.role
    val signedInEmployee = signedInUserManager.employee

    init {
        webSocketInstance.addWebSocketListener(this)

        // Add event bus collection
        viewModelScope.launch {
            EventBus.events.collect { event ->
                when (event) {
                    is EventBus.JobCardEvent.StatusChanged -> {
                        // Refresh job card status when event is received
                        refreshJobCardStatus()
                    }

                    is EventBus.JobCardEvent.Error -> {
                        _statusLoadingState.value = LoadingState.Error(event.message)
                    }

                    is EventBus.JobCardEvent.ReportCRUD -> {}
                }
            }
        }

        viewModelScope.launch {
            EventBus.timesheetEvent.collect { event ->
                when (event) {
                    EventBus.TimesheetEvent.Error -> _statusLoadingState.value =
                        LoadingState.Error("Encountered error, refresh Job Card")

                    EventBus.TimesheetEvent.NewTimesheet -> refreshJobCardStatus()
                }
            }
        }

        viewModelScope.launch {
            EventBus.commentEvent.collect { event ->
                when (event) {
                    EventBus.CommentEvent.CommentCreated -> refreshJobCard()
                    EventBus.CommentEvent.CommentDeleted -> refreshJobCard()
                }
            }
        }

        viewModelScope.launch {
            fetchJobCard()
            fetchJobCardCardStatus()
        }
    }

    fun refreshJobCard() {
        _loadingState.value = LoadingState.Loading
        _statusLoadingState.value = LoadingState.Loading
        _jobCard.value = null
        _jobCardStatusList.value = emptyList()
        viewModelScope.launch {
            fetchJobCard()
            fetchJobCardCardStatus()
        }
    }

    private fun refreshJobCardStatus() {
        _statusLoadingState.value = LoadingState.Loading
        _jobCardStatusList.value = emptyList()
        viewModelScope.launch {
            fetchJobCardCardStatus()
        }
    }


    private suspend fun fetchJobCardCardStatus() {
        _statusLoadingState.value = LoadingState.Loading
        try {
            when (val response =
                jobCardStatusRepository.getJobCardStatusesByJobId(UUID.fromString(jobId))) {
                is JobCardStatusRepository.LoadingResult.Error -> _statusLoadingState.value =
                    LoadingState.Error(response.message)

                is JobCardStatusRepository.LoadingResult.Loading -> _statusLoadingState.value =
                    LoadingState.Loading

                is JobCardStatusRepository.LoadingResult.Success -> {
                    _jobCardStatusList.value = response.status
                    _statusLoadingState.value = LoadingState.Success
                }
            }

        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingState.Error("Network Error")
                else -> LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun updateJobCardsStatus(newStatus: String) {
        _statusLoadingState.value = LoadingState.Loading
        try {
            when (val response =
                jobCardStatusRepository.addNewJobCardStatus(UUID.fromString(jobId), newStatus)) {
                is JobCardStatusRepository.LoadingResult.Error -> _statusLoadingState.value =
                    LoadingState.Error(response.message)

                is JobCardStatusRepository.LoadingResult.Loading -> _statusLoadingState.value =
                    LoadingState.Loading

                is JobCardStatusRepository.LoadingResult.Success -> {
                    EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobId)))
                    webSocketInstance.sendWebSocketMessage(
                        "JOB_CARD_STATUS_CHANGED",
                        UUID.fromString(jobId)
                    )
                    _jobCardStatusList.value = response.status
                    _statusLoadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingState.Error("Network Error")
                else -> LoadingState.Error(e.message ?: "Unknown Error")
            }
        } finally {
            fetchJobCardCardStatus()
        }
    }

    private val _deleteJobCardConfirmation = MutableStateFlow(false)
    val deleteJobCardConfirmation = _deleteJobCardConfirmation.asStateFlow()

    fun setDeleteJobCardConfirmation(value: Boolean) {
        _deleteJobCardConfirmation.value = value
    }


    private fun updateJobCard(update: JobCard.() -> JobCard) {
        _jobCard.value = _jobCard.value?.update()
        saveJobCard()
    }

    fun updateDateAndTimeIn(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeIn = newDateTime) }
        viewModelScope.launch {
            updateJobCardsStatus("opened")
        }
    }

    fun updateEstimatedTimeOfCompletion(newDateTime: LocalDateTime) {
        if (_jobCard.value?.jobCardDeadline == null) {
            updateJobCard {
                copy(
                    estimatedTimeOfCompletion = newDateTime,
                    jobCardDeadline = newDateTime
                )
            }
        } else {
            updateJobCard { copy(estimatedTimeOfCompletion = newDateTime) }
        }

    }

    fun togglePriority() {
        updateJobCard { copy(priority = !_jobCard.value?.priority!!) }
    }

    fun updateJobCardDeadline(newDateTime: LocalDateTime) {
        updateJobCard { copy(jobCardDeadline = newDateTime) }
    }

    fun updateDateAndTimeClosed(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeClosed = newDateTime) }
        viewModelScope.launch {
            updateJobCardsStatus("done")
        }
    }

    fun updateDateAndTimeFrozen(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeFrozen = newDateTime) }
        viewModelScope.launch {
            updateJobCardsStatus("onhold")
        }
    }

    private suspend fun fetchJobCard() {
        _loadingState.value = LoadingState.Loading
        try {
            when (val response = jobCardRepository.getJobCard(UUID.fromString(jobId))) {
                is JobCardRepository.LoadingResult.Error -> {
                    _loadingState.value = LoadingState.Error(response.message)
                }

                is JobCardRepository.LoadingResult.ErrorSingleMessage -> {
                    _loadingState.value = LoadingState.Error(response.message)
                }

                is JobCardRepository.LoadingResult.NetworkError -> {
                    _loadingState.value = LoadingState.Error("Network Error")
                }

                is JobCardRepository.LoadingResult.SingleEntity -> {
                    _jobCard.value = response.jobCard
                    _loadingState.value = LoadingState.Success
                }

                is JobCardRepository.LoadingResult.Success -> {
                    //will never happen for as single entity
                }
            }

        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    _loadingState.value = LoadingState.Error(e.message ?: "Network Error")
                }

                else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }


    fun saveJobCard() {
        viewModelScope.launch {
            _savingState.value = SaveState.Saving
            try {
                when (val response =
                    jobCardRepository.updateJobCard(_jobCard.value!!.id, _jobCard.value!!)) {
                    is JobCardRepository.LoadingResult.Error -> _savingState.value =
                        SaveState.Error(response.message)

                    is JobCardRepository.LoadingResult.ErrorSingleMessage -> _savingState.value =
                        SaveState.Error(response.message)

                    is JobCardRepository.LoadingResult.NetworkError -> _savingState.value =
                        SaveState.Error("Network Error")

                    is JobCardRepository.LoadingResult.SingleEntity -> {
                        _jobCard.value = response.jobCard
                        webSocketInstance.sendWebSocketMessage(
                            "UPDATE_JOB_CARD",
                            response.jobCard.id
                        )
                        _savingState.value = SaveState.Success
                    }

                    is JobCardRepository.LoadingResult.Success -> _savingState.value =
                        SaveState.Error("Returned list instead of single entity")
                }

            } catch (e: Exception) {
                when (e) {
                    is IOException -> _savingState.value = SaveState.Error("Network Error")
                    else -> _savingState.value = SaveState.Error(e.message ?: "Unknown Error")
                }
            } finally {
                fetchJobCardCardStatus()
            }

        }

    }

    fun deleteJobCard() {
        viewModelScope.launch {
            try {
                jobCardRepository.deleteJobCard(jobCard.value!!.id)
                EventBus.emit(EventBus.JobCardCRUDEvent.JobCardDeleted(jobCard.value!!.id))
                webSocketInstance.sendWebSocketMessage("DELETE_JOB_CARD", jobCard.value!!.id)
            } catch (e: Exception) {
                throw e
            }

        }

    }

    fun resetSaveState() {
        _savingState.value = SaveState.Idle
    }

    fun refreshStatusList() {
        viewModelScope.launch {
            fetchJobCardCardStatus()
        }
    }

    sealed class SaveState {
        data object Idle : SaveState()
        data object Saving : SaveState()
        data object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    sealed class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        viewModelScope.launch {
            when (update) {
                is WebSocketUpdate.JobCardCreated -> refreshJobCard()
                is WebSocketUpdate.JobCardUpdated -> refreshJobCard()
                is WebSocketUpdate.StatusChanged -> refreshStatusList()
                is WebSocketUpdate.JobCardDeleted -> refreshJobCard()
                is WebSocketUpdate.NewComment -> refreshJobCard()
                is WebSocketUpdate.DeleteComment -> refreshJobCard()
                else -> {}
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {
    }
}
