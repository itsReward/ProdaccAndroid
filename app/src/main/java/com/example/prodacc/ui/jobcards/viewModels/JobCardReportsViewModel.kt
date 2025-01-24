package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel.LoadingState
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.repositories.JobCardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class JobCardReportsViewModel(
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    val jobId: String
): ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _jobCardReports = MutableStateFlow<List<JobCardReport>>(emptyList())
    private val jobCardReports = _jobCardReports.asStateFlow()

    init {
        WebSocketInstance.addWebSocketListener(this)
        viewModelScope.launch {
            fetchJobCardReports()
        }
    }

    // States for each report type
    private val _serviceAdvisorReport = MutableStateFlow<JobCardReport?>(null)
    val serviceAdvisorReport = _serviceAdvisorReport.asStateFlow()


    //Reports Loading State
    private val _serviceAdvisorReportLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
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

    fun refreshReports(){
        _serviceAdvisorReportLoadingState.value  = LoadingState.Loading
        _jobCardReports.value = emptyList()
        _serviceAdvisorReport.value = null
        viewModelScope.launch {
            fetchJobCardReports()
        }
    }



    fun editServiceAdvisorReport(newReport: String){
        _serviceAdvisorReport.value = _serviceAdvisorReport.value?.copy(jobReport = newReport)?:
            JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.fromString(jobId),
                employeeId = SignedInUser.user!!.employeeId,
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
                    when (val response = jobCardReportRepository.updateJobCardReport(report.reportId, report)){
                        is JobCardReportRepository.LoadingResult.Error -> _serviceAdvisorReportLoadingState.value = LoadingState.Error(response.message)
                        is JobCardReportRepository.LoadingResult.SingleEntitySuccess -> {
                            EventBus.emit(EventBus.JobCardEvent.ReportCRUD(UUID.fromString(jobId)))
                            _serviceAdvisorReport.value = response.response
                            WebSocketInstance.sendWebSocketMessage("UPDATE_JOB_CARD_REPORT", response.response.jobCardId)
                            _serviceAdvisorReportLoadingState.value = LoadingState.Success
                        }
                        is JobCardReportRepository.LoadingResult.Success -> {
                            //will never happen
                        }
                    }

                } else {
                    // Create new report
                    when(val response = jobCardReportRepository.newJobCardReport(report)){
                        is JobCardReportRepository.LoadingResult.Error -> _serviceAdvisorReportLoadingState.value = LoadingState.Error(response.message)
                        is JobCardReportRepository.LoadingResult.SingleEntitySuccess -> {
                            EventBus.emit(EventBus.JobCardEvent.ReportCRUD(UUID.fromString(jobId)))
                            WebSocketInstance.sendWebSocketMessage("NEW_JOB_CARD_REPORT", response.response.jobCardId)
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

    open class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message: String): LoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.NewReport -> {
                if (update.id == UUID.fromString(jobId)){
                    refreshReports()
                }
            }
            is WebSocketUpdate.UpdateReport -> {
                if (update.id == UUID.fromString(jobId)){
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

class JobCardReportsViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(JobCardReportsViewModel::class.java)) {
            return JobCardReportsViewModel(jobId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}