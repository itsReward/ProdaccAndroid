package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.repositories.JobCardReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobCardReportsViewModel @Inject constructor(
    private val jobCardReportRepository: JobCardReportRepository,
    private val webSocketInstance: WebSocketInstance,
    private val signedInUserManager: SignedInUserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get jobCardId from SavedStateHandle
    private val jobId: String = checkNotNull(savedStateHandle["jobCardId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    private val _jobCardReports = MutableStateFlow<List<JobCardReport>>(emptyList())
    private val jobCardReports = _jobCardReports.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)
        viewModelScope.launch {
            fetchJobCardReports()
        }
    }

    // States for each report type
    private val _serviceAdvisorReport = MutableStateFlow<JobCardReport?>(null)
    val serviceAdvisorReport = _serviceAdvisorReport.asStateFlow()


    //Reports Loading State
    private val _serviceAdvisorReportLoadingState =
        MutableStateFlow<LoadingState>(LoadingState.Idle)
    val serviceAdvisorReportLoadingState = _serviceAdvisorReportLoadingState.asStateFlow()


    // Report editing states
    private val _isServiceAdvisorReportEdited = MutableStateFlow(false)
    val isServiceAdvisorReportEdited = _isServiceAdvisorReportEdited.asStateFlow()


    private fun fetchJobCardReports() {
        viewModelScope.launch {
            when (val response =
                jobCardReportRepository.getJobCardReports(UUID.fromString(jobId))) {
                is JobCardReportRepository.LoadingResult.Success -> {
                    val reports = response.response
                    _jobCardReports.value = reports

                    // Populate individual report states
                    _serviceAdvisorReport.value =
                        reports.find { it.reportType == "serviceAdvisorReport" }

                    _serviceAdvisorReportLoadingState.value = LoadingState.Success

                }

                else -> { /* Handle errors */
                    _serviceAdvisorReportLoadingState.value = LoadingState.Error("Error")
                }
            }
        }
    }

    fun refreshReports() {
        _serviceAdvisorReportLoadingState.value = LoadingState.Loading
        _jobCardReports.value = emptyList()
        _serviceAdvisorReport.value = null
        viewModelScope.launch {
            fetchJobCardReports()
        }
    }


    fun editServiceAdvisorReport(newReport: String) {
        _serviceAdvisorReport.value =
            _serviceAdvisorReport.value?.copy(jobReport = newReport) ?: JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.fromString(jobId),
                employeeId = signedInUserManager.employee.value!!.id,
                jobReport = newReport,
                reportType = "serviceAdvisorReport"
            )
        _isServiceAdvisorReportEdited.value = true
    }

    fun saveServiceAdvisorReport() {
        _serviceAdvisorReport.value?.let { report ->
            _serviceAdvisorReportLoadingState.value = LoadingState.Loading
            saveReport(report)
            _isServiceAdvisorReportEdited.value = false
        }
    }


    private fun saveReport(report: JobCardReport) {
        viewModelScope.launch {
            try {
                if (jobCardReports.value.any { it.reportType == report.reportType }) {
                    // Update existing report
                    when (val response =
                        jobCardReportRepository.updateJobCardReport(report.reportId, report)) {
                        is JobCardReportRepository.LoadingResult.Error -> _serviceAdvisorReportLoadingState.value =
                            LoadingState.Error(response.message)

                        is JobCardReportRepository.LoadingResult.SingleEntitySuccess -> {
                            EventBus.emit(EventBus.JobCardEvent.ReportCRUD(UUID.fromString(jobId)))
                            _serviceAdvisorReport.value = response.response
                            webSocketInstance.sendWebSocketMessage(
                                "UPDATE_JOB_CARD_REPORT",
                                response.response.jobCardId
                            )
                            _serviceAdvisorReportLoadingState.value = LoadingState.Success
                        }

                        is JobCardReportRepository.LoadingResult.Success -> {
                            //will never happen
                        }
                    }

                } else {
                    // Create new report
                    when (val response = jobCardReportRepository.newJobCardReport(report)) {
                        is JobCardReportRepository.LoadingResult.Error -> _serviceAdvisorReportLoadingState.value =
                            LoadingState.Error(response.message)

                        is JobCardReportRepository.LoadingResult.SingleEntitySuccess -> {
                            EventBus.emit(EventBus.JobCardEvent.ReportCRUD(UUID.fromString(jobId)))
                            webSocketInstance.sendWebSocketMessage(
                                "NEW_JOB_CARD_REPORT",
                                response.response.jobCardId
                            )
                            _serviceAdvisorReport.value = response.response
                        }

                        is JobCardReportRepository.LoadingResult.Success -> {
                            //Will never happen
                        }
                    }

                }
                fetchJobCardReports() // Refresh reports after save/update
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    open class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when (update) {
            is WebSocketUpdate.NewReport -> {
                if (update.id == UUID.fromString(jobId)) {
                    refreshReports()
                }
            }

            is WebSocketUpdate.UpdateReport -> {
                if (update.id == UUID.fromString(jobId)) {
                    refreshReports()
                }
            }

            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        //nothing to do here
    }
}
