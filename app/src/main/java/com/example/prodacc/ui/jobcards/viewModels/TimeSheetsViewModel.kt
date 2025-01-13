package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel.LoadingState
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.CreateTimesheet
import com.prodacc.data.remote.dao.NewTimesheet
import com.prodacc.data.remote.dao.Timesheet
import com.prodacc.data.repositories.JobCardStatusRepository
import com.prodacc.data.repositories.TimeSheetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class TimeSheetsViewModel(
    private val timeSheetRepository: TimeSheetRepository = TimeSheetRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val signedInUser: SignedInUser = SignedInUser,
    private val jobCardId: String
) : ViewModel() {
    private val _timeSheets = MutableStateFlow<List<Timesheet>>(emptyList())
    val timeSheets = _timeSheets.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _updateLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val updateLoadingState = _updateLoadingState.asStateFlow()

    private val _statusLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val statusLoadingState = _statusLoadingState.asStateFlow()

    private val _newTimeSheetLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newTimeSheetLoadState = _newTimeSheetLoadState.asStateFlow()

    private var newTimeSheetCount = 0
    
    private val _isSavingNewTimeSheet = AtomicBoolean(false)

    //Diagnostics time sheet
    private val _diagnosticsClockIn = MutableStateFlow<LocalDateTime?>(null)
    val diagnosticsClockIn = _diagnosticsClockIn.asStateFlow()

    private val _diagnosticsClockOut = MutableStateFlow<LocalDateTime?>(null)
    val diagnosticsClockOut = _diagnosticsClockOut.asStateFlow()

    private val _diagnosticsReport = MutableStateFlow<String>("")
    val diagnosticsReport = _diagnosticsReport.asStateFlow()

    private val _isDiagnosticsReportEdited = MutableStateFlow(false)
    val isDiagnosticsReportEdited = _isDiagnosticsReportEdited.asStateFlow()

    private val _diagnosticsTimeSheet = MutableStateFlow<Timesheet?>(null)
    val diagnosticsTimeSheet = _diagnosticsTimeSheet.asStateFlow()

    private val _newDiagnosticsTimeSheetLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newDiagnosticsTimeSheetLoadState = _newDiagnosticsTimeSheetLoadState.asStateFlow()

    private val _updatingDiagnosticsTimeSheetLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val updatingDiagnosticsTimeSheetLoadState = _updatingDiagnosticsTimeSheetLoadState.asStateFlow()

    //Control Report time sheet
    private val _controlClockIn = MutableStateFlow<LocalDateTime?>(null)
    val controlClockIn = _controlClockIn.asStateFlow()

    private val _controlClockOut = MutableStateFlow<LocalDateTime?>(null)
    val controlClockOut = _controlClockOut.asStateFlow()

    private val _controlReport = MutableStateFlow<String>("")
    val controlReport = _controlReport.asStateFlow()

    private val _isControlReportEdited = MutableStateFlow(false)
    val isControlReportEdited = _isControlReportEdited.asStateFlow()

    private val _controlTimeSheet = MutableStateFlow<Timesheet?>(null)
    val controlTimeSheet = _controlTimeSheet.asStateFlow()



    //New TimeSheet
    private val _newTimeSheet = MutableStateFlow<CreateTimesheet>(
        CreateTimesheet(
            technicianId = signedInUser.user!!.employeeId,
            jobCardId = UUID.fromString(jobCardId)
        )
    )
    val newTimeSheet = _newTimeSheet.asStateFlow()

    init {
        viewModelScope.launch {
            fetchTimeSheets()
        }
    }

    fun onTitleChange(title: String) {
        _newTimeSheet.value = _newTimeSheet.value.copy(sheetTitle = title)
    }

    fun onReportChange(report: String) {
        _newTimeSheet.value = _newTimeSheet.value.copy(report = report)
    }

    fun clockIn(date: LocalDateTime) {
        _newTimeSheet.value = _newTimeSheet.value.copy(clockInDateAndTime = date)
    }

    fun clockOut(date: LocalDateTime) {
        _newTimeSheet.value = _newTimeSheet.value.copy(clockOutDateAndTime = date)
    }

    fun updateDiagnosticsClockIn(date: LocalDateTime){
        _diagnosticsClockIn.value = date
        viewModelScope.launch {
            try {
                addDiagnosticsTimesheet(
                    NewTimesheet(
                        sheetTitle = "Diagnostics",
                        report = "",
                        clockInDateAndTime = date,
                        clockOutDateAndTime = null,
                        jobCardId = UUID.fromString(jobCardId),
                        employeeId = signedInUser.user!!.employeeId
                    )
                )
                fetchTimeSheets()
            } finally {
                updateJobCardsStatus("diagnostics")
            }


        }

    }
    fun updateDiagnosticsClockOut(date: LocalDateTime){
        _diagnosticsClockOut.value = date
        _diagnosticsTimeSheet.value = _diagnosticsTimeSheet.value!!.copy(clockOutDateAndTime = date)
        viewModelScope.launch {
            try {
                updateDiagnosticsTimeSheet(_diagnosticsTimeSheet.value!!)
            } finally {
                fetchTimeSheets()
                updateJobCardsStatus("approval")
            }


        }

    }
    fun updateDiagnosticsReport(report: String){
        _isDiagnosticsReportEdited.value = true
        _diagnosticsReport.value = report
    }

    fun onSaveDiagnosticsReport(){
        _diagnosticsTimeSheet.value = _diagnosticsTimeSheet.value!!.copy(report = _diagnosticsReport.value)
        viewModelScope.launch {
            try {
                updateDiagnosticsTimeSheet(_diagnosticsTimeSheet.value!!)
            } finally {
                _isDiagnosticsReportEdited.value = false
                fetchTimeSheets()
            }
        }
    }


    fun saveTimesheet() {
        if (_isSavingNewTimeSheet.compareAndSet(false, true)) {
            viewModelScope.launch {
                try {
                    addTimesheets()
                } finally {
                    _isSavingNewTimeSheet.set(false)
                }

            }
        }

    }

    private suspend fun fetchTimeSheets() {
        _loadingState.value = LoadingState.Loading
        try {
            when (val response =
                timeSheetRepository.getJobCardTimeSheets(UUID.fromString(jobCardId))) {
                is TimeSheetRepository.LoadingResult.Error -> {
                    _loadingState.value = LoadingState.Error(response.message)
                }

                is TimeSheetRepository.LoadingResult.Message -> {
                    _loadingState.value = LoadingState.Error("Returned Message")
                }

                is TimeSheetRepository.LoadingResult.Success -> {
                    _timeSheets.value = response.timesheets
                    if (response.timesheets.isNotEmpty()) {
                        response.timesheets.find { it.sheetTitle == "Diagnostics" }.let {
                            if (it != null) {
                                _diagnosticsTimeSheet.value = it
                                _diagnosticsClockIn.value = it.clockInDateAndTime
                                _diagnosticsClockOut.value = it.clockOutDateAndTime
                                _diagnosticsReport.value = it.report
                                it
                            } else {
                                null
                            }
                        }
                    }

                    _loadingState.value = LoadingState.Success
                }

                is TimeSheetRepository.LoadingResult.TimeSheet -> {
                    _loadingState.value = LoadingState.Error("Returned timesheet instead of list")
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

    private suspend fun addDiagnosticsTimesheet(diagnosticsTimesheet: NewTimesheet) {

        _newTimeSheetLoadState.value = LoadingState.Loading
        when (val response = timeSheetRepository.addTimeSheet(diagnosticsTimesheet)) {
            is TimeSheetRepository.LoadingResult.Error -> {
                println(response.message)
                _newDiagnosticsTimeSheetLoadState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Message -> {
                println(response.message)
                _newDiagnosticsTimeSheetLoadState.value = LoadingState.Error("Returned Message")
            }

            is TimeSheetRepository.LoadingResult.Success -> {
                _newDiagnosticsTimeSheetLoadState.value =
                    LoadingState.Error("Returned list instead of Timesheet")
            }

            is TimeSheetRepository.LoadingResult.TimeSheet -> {
                _newDiagnosticsTimeSheetLoadState.value = LoadingState.Success
                _diagnosticsTimeSheet.value = response.timesheet
                fetchTimeSheets()
            }
        }
    }

    private suspend fun addTimesheets() {
        if (newTimeSheetCount == 0){
            updateJobCardsStatus("work_in_progress")
            newTimeSheetCount += 1
        }

        if (_newTimeSheet.value.sheetTitle == null || _newTimeSheet.value.report == null || _newTimeSheet.value.clockInDateAndTime == null) {
            _newTimeSheetLoadState.value = LoadingState.Error("Missing required fields")
        } else {
            _newTimeSheetLoadState.value = LoadingState.Loading
            when (val response =
                timeSheetRepository.addTimeSheet(_newTimeSheet.value.toNewTimesheet())) {
                is TimeSheetRepository.LoadingResult.Error -> {
                    println(response.message)
                    _newTimeSheetLoadState.value = LoadingState.Error(response.message)
                }

                is TimeSheetRepository.LoadingResult.Message -> {
                    println(response.message)
                    _newTimeSheetLoadState.value = LoadingState.Error("Returned Message")
                }

                is TimeSheetRepository.LoadingResult.Success -> {
                    _newTimeSheetLoadState.value =
                        LoadingState.Error("Returned list instead of Timesheet")
                }

                is TimeSheetRepository.LoadingResult.TimeSheet -> _newTimeSheetLoadState.value =
                    LoadingState.Success
            }
        }
    }

    private suspend fun updateDiagnosticsTimeSheet(timeSheet: Timesheet){
        _updateLoadingState.value = LoadingState.Loading
        when (val response =
            timeSheetRepository.updateTimesheet(timeSheet.id, timeSheet)) {
            is TimeSheetRepository.LoadingResult.Error -> {
                println(response.message)
                _updateLoadingState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Message -> {
                println(response.message)
                _updateLoadingState.value = LoadingState.Error("Returned Message")
            }

            is TimeSheetRepository.LoadingResult.Success -> {
                _updateLoadingState.value =
                    LoadingState.Error("Returned list instead of Timesheet")
            }

            is TimeSheetRepository.LoadingResult.TimeSheet -> {
                _diagnosticsTimeSheet.value = response.timesheet
                _updateLoadingState.value =
                    LoadingState.Success
            }
        }
    }

    private suspend fun updateJobCardsStatus(newStatus: String){
        _statusLoadingState.value = LoadingState.Loading
        try {
            when(val response = jobCardStatusRepository.addNewJobCardStatus(UUID.fromString(jobCardId), newStatus)){
                is JobCardStatusRepository.LoadingResult.Error -> _statusLoadingState.value = LoadingState.Error(response.message)
                is JobCardStatusRepository.LoadingResult.Loading -> _statusLoadingState.value = LoadingState.Loading
                is JobCardStatusRepository.LoadingResult.Success -> {
                    _statusLoadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception){
            when (e){
                is IOException -> JobCardDetailsViewModel.LoadingState.Error("Network Error")
                else -> JobCardDetailsViewModel.LoadingState.Error(e.message?:"Unknown Error")
            }
        }
    }


    fun refreshTimeSheets() {
        viewModelScope.launch {
            fetchTimeSheets()
        }
    }


    open class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()

    }

    fun CreateTimesheet.toNewTimesheet(): NewTimesheet {
        return NewTimesheet(
            sheetTitle = this.sheetTitle ?: "__",
            report = this.report ?: "",
            clockInDateAndTime = this.clockInDateAndTime ?: LocalDateTime.now(),
            clockOutDateAndTime = this.clockOutDateAndTime,
            jobCardId = this.jobCardId
                ?: throw IllegalArgumentException("JobCardId cannot be null"),
            employeeId = this.technicianId
                ?: throw IllegalArgumentException("technicianId cannot be null"),
        )
    }

    fun resetNewTimeSheetLoadState() {
        _newTimeSheetLoadState.value = LoadingState.Idle
    }
}

class TimeSheetsViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(TimeSheetsViewModel::class.java)) {
            return TimeSheetsViewModel(jobCardId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

