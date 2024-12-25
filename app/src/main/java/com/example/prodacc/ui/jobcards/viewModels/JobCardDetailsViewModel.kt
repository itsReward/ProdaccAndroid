package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.vehicles.viewModels.EditVehicleDetailsViewModel
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.repositories.JobCardReportRepository
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
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    private val jobId: String
):ViewModel() {
    private val _jobCard = MutableStateFlow<JobCard?>(null)
    val jobCard = _jobCard.asStateFlow()

    private val _jobCardReports = MutableStateFlow<List<JobCardReport>>(emptyList())
    val jobCardReports = _jobCardReports.asStateFlow()

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

    // States for each report type
    private val _serviceAdvisorReport = MutableStateFlow<JobCardReport?>(null)
    val serviceAdvisorReport = _serviceAdvisorReport.asStateFlow()

    private val _diagnosticsReport = MutableStateFlow<JobCardReport?>(null)
    val diagnosticsReport = _diagnosticsReport.asStateFlow()

    private val _controlReport = MutableStateFlow<JobCardReport?>(null)
    val controlReport = _controlReport.asStateFlow()

    // Report editing states
    private val _isServiceAdvisorReportEdited = MutableStateFlow(false)
    val isServiceAdvisorReportEdited = _isServiceAdvisorReportEdited.asStateFlow()

    private val _isDiagnosticsReportEdited = MutableStateFlow(false)
    val isDiagnosticsReportEdited = _isDiagnosticsReportEdited.asStateFlow()

    private val _isControlReportEdited = MutableStateFlow(false)
    val isControlReportEdited = _isControlReportEdited.asStateFlow()



    val timesheets = timeSheetRepository.getTimeSheets()

    val teamExpanded = mutableStateOf(false)

    fun onTeamExpandedToggle(){
        teamExpanded.value = !teamExpanded.value
    }

    fun refreshJobCard() {
        viewModelScope.launch {
            fetchJobCard()
            fetchJobCardReports()
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


    fun updateServiceAdvisorReport(newReport: String) {
        val existingReport = jobCardReports.value.find { it.reportType == "serviceAdvisorReport" }
        _serviceAdvisorReport.value = existingReport?.copy(jobReport = newReport) ?:
                JobCardReport(
                    reportId = UUID.randomUUID(),
                    jobCardId = UUID.fromString(jobId),
                    employeeId = SignedInUser.user!!.employeeId, // Set actual employee ID
                    reportType = "serviceAdvisorReport",
                    jobReport = newReport
                )
        _isServiceAdvisorReportEdited.value = true
    }

    fun editDiagnosticsReport(newReport: String){
        _diagnosticsReport.value = _diagnosticsReport.value?.copy(jobReport = newReport)
    }

    fun editControlReport(newReport: String){
        _controlReport.value = _controlReport.value?.copy(jobReport = newReport)
    }

    fun editServiceAdvisorReport(newReport: String){
        _serviceAdvisorReport.value = _serviceAdvisorReport.value?.copy(jobReport = newReport)
    }

    fun updateDiagnosticsReport(newReport: String) {
        val existingReport = jobCardReports.value.find { it.reportType == "diagnosticsReport" }
        _diagnosticsReport.value = existingReport?.copy(jobReport = newReport) ?:
                JobCardReport(
                    reportId = UUID.randomUUID(),
                    jobCardId = UUID.fromString(jobId),
                    employeeId = SignedInUser.user!!.employeeId, // Set actual employee ID
                    reportType = "diagnosticsReport",
                    jobReport = newReport
                )
        _isDiagnosticsReportEdited.value = true
    }


    fun updateControlReport(newReport: String) {
        val existingReport = jobCardReports.value.find { it.reportType == "controlReport" }
        _controlReport.value = existingReport?.copy(jobReport = newReport) ?:
                JobCardReport(
                    reportId = UUID.randomUUID(),
                    jobCardId = UUID.fromString(jobId),
                    employeeId = SignedInUser.user!!.employeeId, // Set actual employee ID
                    reportType = "diagnosticsReport",
                    jobReport = newReport
                )
        _isControlReportEdited.value = true
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
                    jobCardReportRepository.saveJobCardReport(report)
                }
                fetchJobCardReports() // Refresh reports after save/update
            } catch (e: Exception) {
                // Handle error
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