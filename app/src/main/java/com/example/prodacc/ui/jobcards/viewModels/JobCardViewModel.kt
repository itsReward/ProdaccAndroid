package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketUpdate
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
): ViewModel(), ApiInstance.WebSocketEventListener{
    private val _jobCards = MutableStateFlow<List<JobCard>>(emptyList())
    private val jobCards: StateFlow<List<JobCard>> = _jobCards.asStateFlow()

    private val _filteredJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val filteredJobCards: StateFlow<List<JobCard>> = _filteredJobCards.asStateFlow()

    private val _reportsMap = MutableStateFlow<Map<UUID, ReportLoadingState>>(emptyMap())
    val reportsMap: StateFlow<Map<UUID, ReportLoadingState>> = _reportsMap.asStateFlow()

    private val _statusMap = MutableStateFlow<Map<UUID, StatusLoadingState>>(emptyMap())
    val statusMap: StateFlow<Map<UUID, StatusLoadingState>> = _statusMap.asStateFlow()

    private val _jobCardLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val jobCardLoadState = _jobCardLoadState

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _pastJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val pastJobCards: StateFlow<List<JobCard>> = _pastJobCards.asStateFlow()



    private val _jobCardsFilter = MutableStateFlow<JobCardsFilter>(JobCardsFilter.All())
    val jobCardsFilter = _jobCardsFilter.asStateFlow()

    fun onToggleAllFilterChip(){
        _jobCardsFilter.value = JobCardsFilter.All()
        filterJobCards()
    }

    fun onToggleOpenFilterChip(){
        _jobCardsFilter.value = JobCardsFilter.Open()
        filterJobCards()
    }

    fun onToggleDiagnosticsFilterChip(){
        _jobCardsFilter.value = JobCardsFilter.Diagnostics()
        filterJobCards()
    }

    fun onToggleWorkInProgressChip(){
        _jobCardsFilter.value = JobCardsFilter.WorkInProgress()
        filterJobCards()
    }

    fun onToggleApprovalChip(){
        _jobCardsFilter.value = JobCardsFilter.Approval()
        filterJobCards()
    }

    fun onToggleDoneChip(){
        _jobCardsFilter.value = JobCardsFilter.Done()
        filterJobCards()
    }

    fun onToggleFrozenChip(){
        _jobCardsFilter.value = JobCardsFilter.Frozen()
        filterJobCards()
    }

    fun onToggleTesting(){
        _jobCardsFilter.value = JobCardsFilter.Testing()
        filterJobCards()
    }


    init {
        ApiInstance.addWebSocketListener(this)
        _jobCardLoadState.value = LoadingState.Loading

        viewModelScope.launch {
            EventBus.crudEvents.collect{ event ->
                when (event) {
                    is EventBus.JobCardCRUDEvent.JobCardCreated -> {
                        _jobCards.value += event.jobCard
                        refreshJobCards()
                    }
                    is EventBus.JobCardCRUDEvent.JobCardDeleted -> {
                        refreshJobCards()
                    }
                }

            }
        }

        viewModelScope.launch {
           fetchJobCards()
        }
    }

    private fun filterJobCards() {
        viewModelScope.launch {
            val currentCards = _jobCards.value
            _filteredJobCards.value = when (_jobCardsFilter.value) {
                is JobCardsFilter.All -> currentCards
                is JobCardsFilter.Open -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "opened"
                    } ?: false
                }
                is JobCardsFilter.Approval -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "approval"
                    } ?: false
                }

                is JobCardsFilter.Diagnostics -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "diagnostics"
                    } ?: false
                }
                is JobCardsFilter.Done -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "done"
                    } ?: false
                }
                is JobCardsFilter.Frozen -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "onhold"
                    } ?: false
                }
                is JobCardsFilter.Testing -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "testing"
                    } ?: false
                }
                is JobCardsFilter.WorkInProgress -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "work_in_progress"
                    } ?: false
                }
            }
        }
    }

    private suspend fun fetchJobCards(){

        jobCardRepository.getJobCards().let { loadingResult ->
            when (loadingResult) {
                is JobCardRepository.LoadingResult.Success -> {
                    _jobCardLoadState.value = LoadingState.Success(loadingResult.jobCards)
                    _jobCards.value = loadingResult.jobCards.sortedByDescending { it.dateAndTimeIn }
                    filterJobCards()
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
        _jobCardLoadState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                _refreshing.value = true
                _jobCards.value = emptyList()
                _reportsMap.value = emptyMap()
                _statusMap.value = emptyMap()
                fetchJobCards()
                _jobCards.value.let {
                    it.forEach { jobCard ->
                        fetchJobCardStatus(jobCard.id)
                        fetchJobCardReports(jobCard.id)
                    }
                }
            } finally {
                _refreshing.value = false
            }

        }
    }

    sealed class JobCardsFilter(){
        data class All(val name: String = "all"): JobCardsFilter()
        data class Open(val name: String = "open"): JobCardsFilter()
        data class Approval(val name: String = "approval"): JobCardsFilter()
        data class Diagnostics(val name: String = "diagnostics"): JobCardsFilter()
        data class WorkInProgress(val name: String = "workInProgress"): JobCardsFilter()
        data class Testing(val name: String = "testing"): JobCardsFilter()
        data class Done(val name: String = "done"): JobCardsFilter()
        data class Frozen(val name: String = "frozen"): JobCardsFilter()
    }

    override fun onJobCardUpdate(update: WebSocketUpdate) {
        viewModelScope.launch {
            when(update){
                is WebSocketUpdate.JobCardCreated -> refreshJobCards()
                is WebSocketUpdate.JobCardUpdated -> refreshJobCards()
                is WebSocketUpdate.StatusChanged -> refreshJobCards()
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {
        _jobCardLoadState.value = LoadingState.Error("Connection Error: ${error.message}")
    }

    override fun onCleared() {
        super.onCleared()
        ApiInstance.removeWebSocketListener(this)
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

