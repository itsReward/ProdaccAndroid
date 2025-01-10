package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.repositories.JobCardReportRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class JobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository()
): ViewModel(){
    private val _jobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val jobCards: StateFlow<List<JobCard>> = _jobCards.asStateFlow()



    private val _reportsMap = MutableStateFlow<Map<UUID, ReportLoadingState>>(emptyMap())
    val reportsMap: StateFlow<Map<UUID, ReportLoadingState>> = _reportsMap.asStateFlow()

    private val _statusMap = MutableStateFlow<Map<UUID, StatusLoadingState>>(emptyMap())
    val statusMap: StateFlow<Map<UUID, StatusLoadingState>> = _statusMap.asStateFlow()

    private val _jobCardLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val jobCardLoadState = _jobCardLoadState


    private val _pastJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val pastJobCards: StateFlow<List<JobCard>> = _pastJobCards.asStateFlow()


    init {
        viewModelScope.launch {
           fetchJobCards()
        }
    }

    private suspend fun fetchJobCards(){
        _jobCardLoadState.value = LoadingState.Loading

        jobCardRepository.getJobCards().let { loadingResult ->
            when (loadingResult) {
                is JobCardRepository.LoadingResult.Success -> {
                    _jobCardLoadState.value = LoadingState.Success(loadingResult.jobCards)
                    _jobCards.value = loadingResult.jobCards.sortedByDescending { it.dateAndTimeIn }
                }

                is JobCardRepository.LoadingResult.Error -> {
                    _jobCardLoadState.value = LoadingState.Error(loadingResult.message)
                }

                is JobCardRepository.LoadingResult.ErrorSingleMessage ->_jobCardLoadState.value = LoadingState.Error(loadingResult.message)
                is JobCardRepository.LoadingResult.NetworkError ->_jobCardLoadState.value = LoadingState.Error("Network Error")
                is JobCardRepository.LoadingResult.SingleEntity ->_jobCardLoadState.value = LoadingState.Error("Returned Single Entity Instead of List")
            }
        }

        _pastJobCards.value = jobCards.value.filter { it.dateAndTimeClosed != null }

    }

    fun fetchJobCardReports(jobCardId: UUID) {
        val currentState = _reportsMap.value[jobCardId]
        if (currentState == null || currentState is ReportLoadingState.Error) {
            viewModelScope.launch {
                try {
                    // Update loading state for specific job card
                    _reportsMap.update { currentMap ->
                        currentMap + (jobCardId to ReportLoadingState.Loading)
                    }

                    when (val result = jobCardReportRepository.getJobCardReports(jobCardId)) {
                        is JobCardReportRepository.LoadingResult.Error -> {
                            _reportsMap.update { currentMap ->
                                currentMap + (jobCardId to ReportLoadingState.Error(result.message))
                            }
                        }
                        is JobCardReportRepository.LoadingResult.Success -> {
                            _reportsMap.update { currentMap ->
                                currentMap + (jobCardId to ReportLoadingState.Success(
                                    if (result.response.isEmpty()) {
                                        null
                                    } else {
                                        result.response.first { it.reportType == "serviceAdvisorReport" }
                                    }
                                ))
                            }
                        }
                        is JobCardReportRepository.LoadingResult.SingleEntitySuccess -> {
                            _reportsMap.update { currentMap ->
                                currentMap + (jobCardId to ReportLoadingState.Error("returned single entity instead of list"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    val errorState = when (e) {
                        is IOException -> ReportLoadingState.Error("Network Error")
                        else -> ReportLoadingState.Error(e.message ?: "Unknown Error")
                    }
                    _reportsMap.update { currentMap ->
                        currentMap + (jobCardId to errorState)
                    }
                }
            }
        }

    }

    fun fetchJobCardStatus(jobCardId: UUID) {
        val currentState = _statusMap.value[jobCardId]
        if (currentState == null ) {
            viewModelScope.launch {
                try {
                    _statusMap.update { currentMap ->
                        currentMap + (jobCardId to StatusLoadingState.Loading)
                    }
                    when (val result = jobCardStatusRepository.getJobCardStatusesByJobId(jobCardId)) {
                        is JobCardStatusRepository.LoadingResult.Error -> {
                            _statusMap.update { currentMap ->
                                currentMap + (jobCardId to StatusLoadingState.Error(result.message))
                            }
                        }
                        is JobCardStatusRepository.LoadingResult.Loading -> {
                            _statusMap.update { currentMap ->
                                currentMap + (jobCardId to StatusLoadingState.Loading)
                            }
                        }
                        is JobCardStatusRepository.LoadingResult.Success -> {
                            _statusMap.update { currentMap ->
                                currentMap + (jobCardId to StatusLoadingState.Success(
                                    result.status.last()
                                )
                                        )
                            }
                        }
                    }
                } catch (e:Exception){
                    val errorState = when (e) {
                        is IOException -> StatusLoadingState.Error("Network Error")
                        else -> StatusLoadingState.Error(e.message ?: "Unknown Error")
                    }
                    _statusMap.update { currentMap ->
                        currentMap + (jobCardId to errorState)
                    }
                }
            }
        }

    }

    fun refreshJobCards(){
        viewModelScope.launch {
            fetchJobCards()
        }
    }




}

sealed class LoadingState {
    data object Idle : LoadingState()
    data object Loading : LoadingState()
    data class Success(val response: Any) : LoadingState()
    data class Error(val message: String) : LoadingState()

}

sealed class ReportLoadingState {
    data object Idle : ReportLoadingState()
    data object Loading : ReportLoadingState()
    data class Success(val response: JobCardReport?) : ReportLoadingState()
    data class Error(val message: String) : ReportLoadingState()

}

sealed class StatusLoadingState {
    data object Idle : StatusLoadingState()
    data object Loading : StatusLoadingState()
    data class Success(val response: JobCardStatus?) : StatusLoadingState()
    data class Error(val message: String) : StatusLoadingState()

}