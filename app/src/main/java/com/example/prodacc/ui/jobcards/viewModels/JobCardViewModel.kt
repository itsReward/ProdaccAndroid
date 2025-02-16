package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.repositories.JobCardReportRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import com.prodacc.data.repositories.JobCardTechnicianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class JobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val jobCardTechnicianRepository: JobCardTechnicianRepository = JobCardTechnicianRepository()
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _jobCards = MutableStateFlow<List<JobCard>>(emptyList())

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


    private val _jobCardsFilter = MutableStateFlow<JobCardsFilter>(JobCardsFilter.All())
    val jobCardsFilter = _jobCardsFilter.asStateFlow()

    fun onToggleAllFilterChip() {
        _jobCardsFilter.value = JobCardsFilter.All()
        filterJobCards()
    }

    fun onToggleOpenFilterChip() {
        _jobCardsFilter.value = JobCardsFilter.Open()
        filterJobCards()
    }

    fun onToggleDiagnosticsFilterChip() {
        _jobCardsFilter.value = JobCardsFilter.Diagnostics()
        filterJobCards()
    }

    fun onToggleWorkInProgressChip() {
        _jobCardsFilter.value = JobCardsFilter.WorkInProgress()
        filterJobCards()
    }

    fun onToggleApprovalChip() {
        _jobCardsFilter.value = JobCardsFilter.Approval()
        filterJobCards()
    }

    fun onToggleDoneChip() {
        _jobCardsFilter.value = JobCardsFilter.Done()
        filterJobCards()
    }

    fun onToggleFrozenChip() {
        _jobCardsFilter.value = JobCardsFilter.Frozen()
        filterJobCards()
    }

    fun onToggleTesting() {
        _jobCardsFilter.value = JobCardsFilter.Testing()
        filterJobCards()
    }

    fun onToggleWaitingForPayment() {
        _jobCardsFilter.value = JobCardsFilter.WaitingForPayment()
        filterJobCards()
    }


    init {
        WebSocketInstance.addWebSocketListener(this)

        _jobCardLoadState.value = LoadingState.Loading

        viewModelScope.launch {
            EventBus.crudEvents.collect { event ->
                when (event) {
                    is EventBus.JobCardCRUDEvent.JobCardCreated -> {
                        _jobCards.value += event.jobCard
                        refreshJobCards()
                    }

                    is EventBus.JobCardCRUDEvent.JobCardDeleted -> {
                        _jobCardLoadState.value = LoadingState.Loading
                        _reportsMap.value = emptyMap()
                        _statusMap.update { currentMap ->
                            currentMap.filterKeys { it != event.jobCardId }
                        }
                        refreshJobCards()
                    }
                }

            }
        }

        viewModelScope.launch {
            EventBus.events.collect { event ->
                when (event) {
                    is EventBus.JobCardEvent.Error -> {}
                    is EventBus.JobCardEvent.StatusChanged ->
                        refreshSingleJobCardStatus(event.jobId)

                    is EventBus.JobCardEvent.ReportCRUD -> {
                        refreshSingleJobCardReport(event.jobId)
                    }
                }
            }
        }

        viewModelScope.launch {
            EventBus.commentEvent.collect() { event ->
                when (event){
                    EventBus.CommentEvent.CommentCreated -> {
                        refreshJobCards()
                    }
                    EventBus.CommentEvent.CommentDeleted -> {
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

                is JobCardsFilter.WaitingForPayment -> currentCards.filter {
                    statusMap.value[it.id]?.let { state ->
                        state is StatusLoadingState.Success &&
                                state.response?.status == "waiting_for_payment"
                    } ?: false
                }
            }
        }
    }

    private suspend fun fetchJobCards() {

        when (SignedInUser.role) {
            is SignedInUser.Role.Technician -> {
                jobCardTechnicianRepository.getJobCardsForTechnician(SignedInUser.employee!!.id)
                    .let { result ->
                        when (result) {
                            is JobCardTechnicianRepository.LoadingResult.Error -> _jobCardLoadState.value =
                                LoadingState.Error(result.message)

                            is JobCardTechnicianRepository.LoadingResult.Loading -> _jobCardLoadState.value =
                                LoadingState.Loading

                            is JobCardTechnicianRepository.LoadingResult.Success -> {
                                val jobCards = mutableListOf<JobCard>()
                                result.list.let {
                                    it.forEach { id ->
                                        when (val response = jobCardRepository.getJobCard(id)) {
                                            is JobCardRepository.LoadingResult.Error -> jobCardLoadState.value =
                                                LoadingState.Error(response.message)

                                            is JobCardRepository.LoadingResult.ErrorSingleMessage -> jobCardLoadState.value =
                                                LoadingState.Error(response.message)

                                            is JobCardRepository.LoadingResult.NetworkError -> jobCardLoadState.value =
                                                LoadingState.Error("Network Error")

                                            is JobCardRepository.LoadingResult.SingleEntity -> jobCards.add(
                                                response.jobCard
                                            )

                                            is JobCardRepository.LoadingResult.Success -> {/* Will never  happen */
                                            }
                                        }
                                    }
                                }
                                _jobCards.value = jobCards.sortedByDescending { it.dateAndTimeIn }
                                _jobCardLoadState.value = LoadingState.Success(_jobCards.value)
                                filterJobCards()
                            }
                        }
                    }
            }

            else -> {
                jobCardRepository.getJobCards().let { loadingResult ->
                    when (loadingResult) {
                        is JobCardRepository.LoadingResult.Success -> {
                            _jobCardLoadState.value = LoadingState.Success(loadingResult.jobCards)
                            _jobCards.value =
                                loadingResult.jobCards.sortedByDescending { it.dateAndTimeIn }
                            filterJobCards()
                        }

                        is JobCardRepository.LoadingResult.Error -> {
                            _jobCardLoadState.value = LoadingState.Error(loadingResult.message)
                        }

                        is JobCardRepository.LoadingResult.ErrorSingleMessage -> _jobCardLoadState.value =
                            LoadingState.Error(loadingResult.message)

                        is JobCardRepository.LoadingResult.NetworkError -> _jobCardLoadState.value =
                            LoadingState.Error("Network Error")

                        is JobCardRepository.LoadingResult.SingleEntity -> _jobCardLoadState.value =
                            LoadingState.Error("Returned Single Entity Instead of List")
                    }
                }

            }
        }


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

    private fun refreshSingleJobCardReport(jobCardId: UUID) {
        viewModelScope.launch {
            try {
                // Only update the reports map for this specific card
                _reportsMap.update { currentMap ->
                    currentMap + (jobCardId to ReportLoadingState.Loading)
                }

                when (val result = jobCardReportRepository.getJobCardReports(jobCardId)) {
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

                    is JobCardReportRepository.LoadingResult.Error -> {
                        _reportsMap.update { currentMap ->
                            currentMap + (jobCardId to ReportLoadingState.Error(result.message))
                        }
                    }

                    else -> {
                        _reportsMap.update { currentMap ->
                            currentMap + (jobCardId to ReportLoadingState.Error("Unexpected response"))
                        }
                    }
                }
            } catch (e: Exception) {
                _reportsMap.update { currentMap ->
                    currentMap + (jobCardId to ReportLoadingState.Error(
                        e.message ?: "Unknown error"
                    ))
                }
            }
        }
    }

    private fun refreshSingleJobCardStatus(jobCardId: UUID) {
        viewModelScope.launch {
            try {
                // Set loading state for this specific card's status
                _statusMap.update { currentMap ->
                    currentMap + (jobCardId to StatusLoadingState.Loading)
                }

                when (val result = jobCardStatusRepository.getJobCardStatusesByJobId(jobCardId)) {
                    is JobCardStatusRepository.LoadingResult.Success -> {
                        _statusMap.update { currentMap ->
                            currentMap + (jobCardId to StatusLoadingState.Success(
                                result.status.lastOrNull()
                            ))
                        }
                    }

                    is JobCardStatusRepository.LoadingResult.Error -> {
                        _statusMap.update { currentMap ->
                            currentMap + (jobCardId to StatusLoadingState.Error(result.message))
                        }
                    }

                    is JobCardStatusRepository.LoadingResult.Loading -> {
                        // We already set the loading state above, so we can ignore this case
                    }
                }
            } catch (e: Exception) {
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

    fun fetchJobCardStatus(jobCardId: UUID) {
        val currentState = _statusMap.value[jobCardId]
        if (currentState == null) {
            viewModelScope.launch {
                try {
                    _statusMap.update { currentMap ->
                        currentMap + (jobCardId to StatusLoadingState.Loading)
                    }
                    when (val result =
                        jobCardStatusRepository.getJobCardStatusesByJobId(jobCardId)) {
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
                } catch (e: Exception) {
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

    fun refreshJobCards() {
        _jobCardLoadState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                _refreshing.value = true
                _jobCards.value = emptyList()
                _filteredJobCards.value = emptyList()
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
                _jobCardLoadState.value = LoadingState.Success(true)
            }

        }
    }



    sealed class JobCardsFilter{
        data class All(val name: String = "all") : JobCardsFilter()
        data class Open(val name: String = "open") : JobCardsFilter()
        data class Approval(val name: String = "approval") : JobCardsFilter()
        data class Diagnostics(val name: String = "diagnostics") : JobCardsFilter()
        data class WorkInProgress(val name: String = "workInProgress") : JobCardsFilter()
        data class Testing(val name: String = "testing") : JobCardsFilter()
        data class WaitingForPayment(val name: String = "waitingForPayment") : JobCardsFilter()
        data class Done(val name: String = "done") : JobCardsFilter()
        data class Frozen(val name: String = "frozen") : JobCardsFilter()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        viewModelScope.launch {
            when (update) {
                is WebSocketUpdate.JobCardCreated -> {
                    when(SignedInUser.role){
                        is SignedInUser.Role.Technician -> {
                            fetchJobCards()
                        }
                        else -> refreshJobCards()
                    }

                }
                is WebSocketUpdate.JobCardUpdated -> refreshJobCards()
                is WebSocketUpdate.StatusChanged -> refreshSingleJobCardStatus(update.jobCardId)
                is WebSocketUpdate.JobCardDeleted -> {
                    // Remove the job card from the list
                    _jobCards.value = _jobCards.value.filter { it.id != update.jobCardId }
                    // Update filtered list
                    filterJobCards()
                    // Clean up related data
                    _reportsMap.value -= update.jobCardId
                    _statusMap.value -= update.jobCardId
                }
                is WebSocketUpdate.NewReport -> refreshSingleJobCardReport(update.id)
                is WebSocketUpdate.UpdateReport -> refreshSingleJobCardReport(update.id)
                is WebSocketUpdate.DeleteReport -> refreshSingleJobCardReport(update.id)
                is WebSocketUpdate.NewTechnician -> {
                    when(SignedInUser.role){
                        is SignedInUser.Role.Technician -> {
                            fetchJobCards()
                        }
                        else -> refreshJobCards()
                    }
                }
                is WebSocketUpdate.DeleteTechnician -> {
                    when(SignedInUser.role){
                        is SignedInUser.Role.Technician -> {
                            fetchJobCards()
                        }
                        else -> refreshJobCards()
                    }
                }
                is WebSocketUpdate.NewComment -> refreshJobCards()
                is WebSocketUpdate.DeleteComment -> refreshJobCards()

                else -> {}
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {
    }

    override fun onCleared() {
        super.onCleared()
        WebSocketInstance.removeWebSocketListener(this)
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

