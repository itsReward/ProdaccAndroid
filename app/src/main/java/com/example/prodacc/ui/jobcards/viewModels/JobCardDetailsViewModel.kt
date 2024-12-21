package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.vehicles.viewModels.EditVehicleDetailsViewModel
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import com.prodacc.data.repositories.TimeSheetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class JobCardDetailsViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val timeSheetRepository: TimeSheetRepository = TimeSheetRepository(),
    private val jobId: String
):ViewModel() {
    private val _jobCard = MutableStateFlow<JobCard?>(null)
    val jobCard = _jobCard.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchJobCard()
        }
    }

    private val _deleteJobCardConfirmation = MutableStateFlow(false)
    val deleteJobCardConfirmation = _deleteJobCardConfirmation.asStateFlow()

    fun setDeleteJobCardConfirmation(value: Boolean){
        _deleteJobCardConfirmation.value = value
    }


    val jobCardStatusList = jobCardStatusRepository.generateJobCardStatus(UUID.randomUUID())
    val timesheets = timeSheetRepository.getTimeSheets()

    val teamExpanded = mutableStateOf(false)

    fun onTeamExpandedToggle(){
        teamExpanded.value = !teamExpanded.value
    }

    fun refreshJobCard() {
        viewModelScope.launch {
            fetchJobCard()
        }
    }

    fun updateJobCard(update: JobCard.() -> JobCard){
        _jobCard.value = _jobCard.value?.update()
    }

    fun updateDateAndTimeIn(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeIn = newDateTime) }
    }

    fun updateEstimatedTimeOfCompletion(newDateTime: LocalDateTime) {
        updateJobCard { copy(estimatedTimeOfCompletion = newDateTime) }
    }

    fun updateJobCardDeadline(newDateTime: LocalDateTime) {
        updateJobCard { copy(jobCardDeadline = newDateTime) }
    }

    fun updateDateAndTimeClosed(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeClosed = newDateTime) }
    }

    fun updateDateAndTimeFrozen(newDateTime: LocalDateTime) {
        updateJobCard { copy(dateAndTimeFrozen = newDateTime) }
    }

    fun updateSupervisor(id : UUID){

    }

    fun updateServiceAdvisor(id : UUID){

    }

    suspend fun fetchJobCard(){
        _loadingState.value = LoadingState.Loading
        try {
            val id = UUID.fromString(jobId)
            val response = jobCardRepository.getJobCard(id)
            when (response){
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

        } catch (e: Exception){
            when (e) {
                is IOException -> {
                    _loadingState.value = LoadingState.Error(e.message ?: "Network Error")
                }
                else -> _loadingState.value = LoadingState.Error(e.message?:"Unknown Error")
            }
        }
    }

    fun updateJobCardStatus(status: String){

    }

    fun deleteJobCard() {
        viewModelScope.launch {
            jobCardRepository.deleteJobCard(jobCard.value!!.id)
        }

    }

    sealed class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message: String): LoadingState()
    }
}

class JobCardDetailsViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(JobCardDetailsViewModel::class.java)) {
            return JobCardDetailsViewModel(jobId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}