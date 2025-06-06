package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
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
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _timeSheets = MutableStateFlow<List<Timesheet>>(emptyList())
    val timeSheets = _timeSheets.asStateFlow()

    // Diagnostics timesheet states
    private val _diagnosticsLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val diagnosticsLoadingState = _diagnosticsLoadingState.asStateFlow()

    private val _savingDiagnosticsState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val savingDiagnosticsState = _savingDiagnosticsState.asStateFlow()

    // Control timesheet states
    private val _controlLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val controlLoadingState = _controlLoadingState.asStateFlow()

    private val _savingControlState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val savingControlState = _savingControlState.asStateFlow()

    // Regular timesheet states
    private val _newTimeSheetState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newTimeSheetState = _newTimeSheetState.asStateFlow()

    private val _updatingTimeSheetState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val updatingTimeSheetState = _updatingTimeSheetState.asStateFlow()


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
    private val _diagnosticsTimeSheet = MutableStateFlow<Timesheet?>(null)
    val diagnosticsTimeSheet = _diagnosticsTimeSheet.asStateFlow()

    private val _diagnosticsClockIn = MutableStateFlow(_diagnosticsTimeSheet.asStateFlow().value?.clockInDateAndTime)
    val diagnosticsClockIn = _diagnosticsClockIn.asStateFlow()

    private val _diagnosticsClockOut = MutableStateFlow(_diagnosticsTimeSheet.asStateFlow().value?.clockOutDateAndTime)
    val diagnosticsClockOut = _diagnosticsClockOut.asStateFlow()

    private val _diagnosticsReport = MutableStateFlow(_diagnosticsTimeSheet.asStateFlow().value?.report ?: "")
    val diagnosticsReport = _diagnosticsReport.asStateFlow()

    private val _isDiagnosticsReportEdited = MutableStateFlow(false)
    val isDiagnosticsReportEdited = _isDiagnosticsReportEdited.asStateFlow()



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

    private val _newControlTimesheetLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newControlTimesheetLoadState = _newControlTimesheetLoadState.asStateFlow()



    //New TimeSheet
    private val _newTimeSheet = MutableStateFlow<CreateTimesheet>(
        CreateTimesheet(
            technicianId = signedInUser.user!!.employeeId,
            jobCardId = UUID.fromString(jobCardId)
        )
    )
    val newTimeSheet = _newTimeSheet.asStateFlow()

    //Update Existing Timesheet
    private val _timesheet = MutableStateFlow<Timesheet?>(null)
    val timesheet = _timesheet.asStateFlow()

    private val _fetchingTimesheet = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val fetchingTimesheet = _fetchingTimesheet.asStateFlow()

    private val _showTimesheetDialog = MutableStateFlow(false)
    val showTimesheetDialog = _showTimesheetDialog.asStateFlow()

    private val _onClickTimesheet = MutableStateFlow(false)
    val onClickTimesheet = _onClickTimesheet.asStateFlow()

    private val _isTimesheetEdited = MutableStateFlow(false)
    val isTimesheetEdited= _isTimesheetEdited.asStateFlow()

    init {
        WebSocketInstance.addWebSocketListener(this)

        viewModelScope.launch {
            EventBus.events.collect { event ->
                when (event) {
                    is EventBus.JobCardEvent.StatusChanged -> {
                        // Refresh status when status changed event is received
                        //updateJobCardsStatus(currentStatus)
                    }
                    is EventBus.JobCardEvent.Error -> {
                        // Handle error event if needed
                        _statusLoadingState.value = LoadingState.Error(event.message)
                    }

                    is EventBus.JobCardEvent.ReportCRUD -> {
                        //don't have to do anything
                    }
                }
            }
        }

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

    fun updateTimeSheetClockOut(date: LocalDateTime){
        _timesheet.value = _timesheet.value!!.copy(clockOutDateAndTime = date)
        _isTimesheetEdited.value = true
    }

    fun updateTimesheetReport(report: String){
        _timesheet.value = _timesheet.value!!.copy(report = report)
        _isTimesheetEdited.value = true
    }

    fun saveUpdatedTimesheet(){
        viewModelScope.launch{
            try{
                updateTimeSheet()
            } finally {
                _isTimesheetEdited.value = false
            }
        }
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

            } finally {
                updateJobCardsStatus("diagnostics")
                EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobCardId)))
                fetchTimeSheets()
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
                updateJobCardsStatus("approval")
                EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobCardId)))
                fetchTimeSheets()

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

    fun updateControlClockIn(date: LocalDateTime){
        _controlClockIn.value = date
        viewModelScope.launch {
            try{
                addControlTimesheet(
                    NewTimesheet(
                        sheetTitle = "Quality Control Test",
                        report = "",
                        clockInDateAndTime = date,
                        clockOutDateAndTime = null,
                        jobCardId = UUID.fromString(jobCardId),
                        employeeId = signedInUser.user!!.employeeId
                    )
                )

            } finally {
                updateJobCardsStatus("testing")
                EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobCardId)))
                fetchTimeSheets()
            }
        }
    }

    fun updateControlClockOut(date: LocalDateTime){
        _controlClockOut.value = date
        _controlTimeSheet.value = _controlTimeSheet.value!!.copy(clockOutDateAndTime = date)
        viewModelScope.launch {
            try {
                updateControlTimeSheet(_controlTimeSheet.value!!)
            } finally {
                updateJobCardsStatus("waiting_for_payment")
                EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobCardId)))
                fetchTimeSheets()
            }
        }

    }

    fun updateControlReport(report: String){
        _isControlReportEdited.value = true
        _controlReport.value = report
    }

    fun onSaveControlReport(){
        _controlTimeSheet.value = _controlTimeSheet.value!!.copy(report = _controlReport.value)
        viewModelScope.launch {
            try {
                updateControlTimeSheet(_controlTimeSheet.value!!)
            } finally {
                _isControlReportEdited.value = false
                fetchTimeSheets()
            }
        }
    }

    fun saveTimesheet() {
        if (_isSavingNewTimeSheet.compareAndSet(false, true)) {
            viewModelScope.launch {
                try {
                    addTimesheets()
                    refreshTimeSheets()
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
                            } else {
                                null
                            }
                        }
                        response.timesheets.find { it.sheetTitle == "Quality Control Test" }.let {
                            if (it != null) {
                                _controlTimeSheet.value = it
                                _controlClockIn.value = it.clockInDateAndTime
                                _controlClockOut.value = it.clockOutDateAndTime
                                _controlReport.value = it.report
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

    fun onClickTimesheet(timeSheet: Timesheet) {
        _timesheet.value = timeSheet
        _showTimesheetDialog.value = true
        _onClickTimesheet.value = true

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
                _diagnosticsTimeSheet.value = response.timesheet
                WebSocketInstance.sendWebSocketMessage("NEW_TIMESHEET", response.timesheet.jobCardId)
                _newDiagnosticsTimeSheetLoadState.value = LoadingState.Success
            }
        }
    }

    private suspend fun addControlTimesheet(controlTimesheet: NewTimesheet) {

        _newTimeSheetLoadState.value = LoadingState.Loading
        when (val response = timeSheetRepository.addTimeSheet(controlTimesheet)) {
            is TimeSheetRepository.LoadingResult.Error -> {
                println(response.message)
                _newControlTimesheetLoadState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Message -> {
                println(response.message)
                _newControlTimesheetLoadState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Success -> {
                _newControlTimesheetLoadState.value =
                    LoadingState.Error("Returned list instead of Timesheet")
            }

            is TimeSheetRepository.LoadingResult.TimeSheet -> {
                _controlTimeSheet.value = response.timesheet
                WebSocketInstance.sendWebSocketMessage("NEW_TIMESHEET", response.timesheet.jobCardId)
                _newControlTimesheetLoadState.value = LoadingState.Success
            }
        }
    }

    private suspend fun addTimesheets() {
        // First check if this is a regular timesheet not diagnostics or quality control)
        if (_newTimeSheet.value.sheetTitle != "Diagnostics" && _newTimeSheet.value.sheetTitle != "Quality Control Test") {
            // Check if this is the first regular timesheet
            val hasRegularTimesheet = _timeSheets.value.any {
                it.sheetTitle != "Diagnostics" && it.sheetTitle != "Quality Control Test"
            }

            // Only update status if this is the first regular timesheet
            if (!hasRegularTimesheet) {
                updateJobCardsStatus("work_in_progress")
                EventBus.emit(EventBus.JobCardEvent.StatusChanged(UUID.fromString(jobCardId)))
            }
        }

        if (_newTimeSheet.value.sheetTitle == null || _newTimeSheet.value.clockInDateAndTime == null) {
            _newTimeSheetState.value = LoadingState.Error("Missing required fields")
        } else {
            _newTimeSheetState.value = LoadingState.Loading
            when (val response =
                timeSheetRepository.addTimeSheet(_newTimeSheet.value.toNewTimesheet())) {
                is TimeSheetRepository.LoadingResult.Error -> {
                    println(response.message)
                    _newTimeSheetState.value = LoadingState.Error(response.message)
                }

                is TimeSheetRepository.LoadingResult.Message -> {
                    println(response.message)
                    _newTimeSheetState.value = LoadingState.Error("Returned Message")
                }

                is TimeSheetRepository.LoadingResult.Success -> {
                    _newTimeSheetState.value =
                        LoadingState.Error("Returned list instead of Timesheet")
                }

                is TimeSheetRepository.LoadingResult.TimeSheet -> {
                    EventBus.emitTimeSheetEvent(EventBus.TimesheetEvent.NewTimesheet)
                    WebSocketInstance.sendWebSocketMessage("NEW_TIMESHEET", response.timesheet.jobCardId)
                    _newTimeSheetState.value =
                        LoadingState.Success

                }
            }
        }
    }

    private suspend fun updateDiagnosticsTimeSheet(timeSheet: Timesheet){
        _savingDiagnosticsState.value = LoadingState.Loading
        when (val response =
            timeSheetRepository.updateTimesheet(timeSheet.id, timeSheet)) {
            is TimeSheetRepository.LoadingResult.Error -> {
                _savingDiagnosticsState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Message -> {
                _savingDiagnosticsState.value = LoadingState.Error("Returned Message")
            }

            is TimeSheetRepository.LoadingResult.Success -> {
                _savingDiagnosticsState.value =
                    LoadingState.Error("Returned list instead of Timesheet")
            }

            is TimeSheetRepository.LoadingResult.TimeSheet -> {
                _diagnosticsTimeSheet.value = response.timesheet
                _diagnosticsClockOut.value = _diagnosticsTimeSheet.value!!.clockOutDateAndTime
                WebSocketInstance.sendWebSocketMessage("UPDATE_TIMESHEET", response.timesheet.jobCardId)
                _savingDiagnosticsState.value =
                    LoadingState.Success
            }
        }
    }

    private suspend fun updateControlTimeSheet(timeSheet: Timesheet){
        _savingControlState.value = LoadingState.Loading
        when (val response =
            timeSheetRepository.updateTimesheet(timeSheet.id, timeSheet)) {
            is TimeSheetRepository.LoadingResult.Error -> {
                println(response.message)
                _savingControlState.value = LoadingState.Error(response.message)
            }

            is TimeSheetRepository.LoadingResult.Message -> {
                println(response.message)
                _savingControlState.value = LoadingState.Error("Returned Message")
            }

            is TimeSheetRepository.LoadingResult.Success -> {
                _savingControlState.value =
                    LoadingState.Error("Returned list instead of Timesheet")
            }

            is TimeSheetRepository.LoadingResult.TimeSheet -> {
                _controlTimeSheet.value = response.timesheet
                WebSocketInstance.sendWebSocketMessage("UPDATE_TIMESHEET", response.timesheet.jobCardId)
                _savingControlState.value =
                    LoadingState.Success
            }
        }
    }

    private suspend fun updateTimeSheet(){
        _updatingTimeSheetState.value = LoadingState.Loading
        try {
            when (val response =
                timeSheetRepository.updateTimesheet(_timesheet.value!!.id, _timesheet.value!!)) {
                is TimeSheetRepository.LoadingResult.Error -> {
                    _updatingTimeSheetState.value = LoadingState.Error(response.message)
                }

                is TimeSheetRepository.LoadingResult.Message -> {
                    _updatingTimeSheetState.value = LoadingState.Error("Returned Message")
                }

                is TimeSheetRepository.LoadingResult.Success -> {
                    _updatingTimeSheetState.value =
                        LoadingState.Error("Returned list instead of Timesheet")
                }

                is TimeSheetRepository.LoadingResult.TimeSheet -> {
                    timeSheetDialogDismissRequest()
                    _timesheet.value = response.timesheet
                    WebSocketInstance.sendWebSocketMessage("UPDATE_TIMESHEET", response.timesheet.jobCardId)
                    _updatingTimeSheetState.value = LoadingState.Success
                }
            }
        } finally {
            fetchTimeSheets()
        }

    }

    private suspend fun updateJobCardsStatus(newStatus: String){
        _statusLoadingState.value = LoadingState.Loading
        try {
            when(val response = jobCardStatusRepository.addNewJobCardStatus(UUID.fromString(jobCardId), newStatus)){
                is JobCardStatusRepository.LoadingResult.Error -> _statusLoadingState.value = LoadingState.Error(response.message)
                is JobCardStatusRepository.LoadingResult.Loading -> _statusLoadingState.value = LoadingState.Loading
                is JobCardStatusRepository.LoadingResult.Success -> {
                    WebSocketInstance.sendWebSocketMessage("JOB_CARD_STATUS_CHANGED", jobCardId)
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
        _loadingState.value = LoadingState.Loading
        _timeSheets.value = emptyList()
        _controlTimeSheet.value = null
        _diagnosticsTimeSheet.value = null
        _timesheet.value = null
        viewModelScope.launch {
            fetchTimeSheets()
        }
    }

    fun timeSheetDialogDismissRequest(){
        _showTimesheetDialog.value = false
        _onClickTimesheet.value = false
    }


    open class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()

    }

    private fun CreateTimesheet.toNewTimesheet(): NewTimesheet {
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
        _newTimeSheetState.value = LoadingState.Idle
        _newTimeSheet.value = CreateTimesheet(
            technicianId = SignedInUser.employee!!.id,
            jobCardId = UUID.fromString(jobCardId)
        )

    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.NewTimesheet -> {
                if (update.id == UUID.fromString(jobCardId)){
                    newTimeSheetCount++
                    refreshTimeSheets()
                }
            }
            is WebSocketUpdate.UpdateTimesheet -> {
                if (update.id == UUID.fromString(jobCardId)){
                    refreshTimeSheets()
                }
            }
            is WebSocketUpdate.DeleteTimesheet -> {
                if (update.id == UUID.fromString(jobCardId)){
                    refreshTimeSheets()
                }
            }
            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
       //nothing to do here
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

