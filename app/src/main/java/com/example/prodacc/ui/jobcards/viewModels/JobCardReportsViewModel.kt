package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel.LoadingState
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.repositories.JobCardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class JobCardReportsViewModel(
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    val jobId: String
): ViewModel() {
    private val _jobCardReports = MutableStateFlow<List<JobCardReport>>(emptyList())
    val jobCardReports = _jobCardReports.asStateFlow()

    init {
        viewModelScope.launch {
            fetchJobCardReports()
        }
    }

    // States for each report type
    private val _serviceAdvisorReport = MutableStateFlow<JobCardReport?>(null)
    val serviceAdvisorReport = _serviceAdvisorReport.asStateFlow()

    private val _diagnosticsReport = MutableStateFlow<JobCardReport?>(null)
    val diagnosticsReport = _diagnosticsReport.asStateFlow()

    private val _controlReport = MutableStateFlow<JobCardReport?>(null)
    val controlReport = _controlReport.asStateFlow()

    //Reports Loading State
    private val _serviceAdvisorReportLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val serviceAdvisorReportLoadingState = _serviceAdvisorReportLoadingState.asStateFlow()

    private val _diagnosticsLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val diagnosticsReportLoadingState = _diagnosticsLoadingState.asStateFlow()

    private val _controlReportLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val controlReportLoadingState = _controlReportLoadingState

    // Report editing states
    private val _isServiceAdvisorReportEdited = MutableStateFlow(false)
    val isServiceAdvisorReportEdited = _isServiceAdvisorReportEdited.asStateFlow()

    private val _isDiagnosticsReportEdited = MutableStateFlow(false)
    val isDiagnosticsReportEdited = _isDiagnosticsReportEdited.asStateFlow()

    private val _isControlReportEdited = MutableStateFlow(false)
    val isControlReportEdited = _isControlReportEdited.asStateFlow()

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
                    _diagnosticsReport.value = reports.find { it.reportType == "diagnosticsReport" }
                    _controlReport.value = reports.find { it.reportType == "controlReport" }
                }

                else -> { /* Handle errors */
                }
            }
        }
    }

    fun editDiagnosticsReport(newReport: String){
        _diagnosticsReport.value = _diagnosticsReport.value?.copy(jobReport = newReport).let {
            JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.fromString(jobId),
                employeeId = SignedInUser.user!!.employeeId,
                jobReport = newReport,
                reportType = "diagnosticsReport"
            )
        }
        _isDiagnosticsReportEdited.value = true
    }

    fun editControlReport(newReport: String){
        _controlReport.value = _controlReport.value?.copy(jobReport = newReport).let {
            JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.fromString(jobId),
                employeeId = SignedInUser.user!!.employeeId,
                jobReport = newReport,
                reportType = "controlReport"
            )
        }
        _isControlReportEdited.value = true
    }

    fun editServiceAdvisorReport(newReport: String){
        _serviceAdvisorReport.value = _serviceAdvisorReport.value?.copy(jobReport = newReport).let {
            JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.fromString(jobId),
                employeeId = SignedInUser.user!!.employeeId,
                jobReport = newReport,
                reportType = "serviceAdvisorReport"
            )
        }
        _isServiceAdvisorReportEdited.value = true
    }

    fun saveServiceAdvisorReport() {
        _serviceAdvisorReport.value?.let { report ->
            saveReport(report)
            _isServiceAdvisorReportEdited.value = false
        }
    }

    fun saveDiagnosticsReport() {
        _diagnosticsReport.value?.let { report ->
            saveReport(report)
            _isDiagnosticsReportEdited.value = false
        }
    }

    fun saveControlReport() {
        _controlReport.value?.let { report ->
            saveReport(report)
            _isControlReportEdited.value = false
            println(report)
        }
    }

    private fun saveReport(report: JobCardReport) {
        viewModelScope.launch {
            try {
                if (jobCardReports.value.any { it.reportType == report.reportType }) {
                    // Update existing report
                    jobCardReportRepository.updateJobCardReport(report)
                } else {
                    // Create new report
                    jobCardReportRepository.newJobCardReport(report)
                }
                fetchJobCardReports() // Refresh reports after save/update
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    sealed class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message: String): LoadingState()
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