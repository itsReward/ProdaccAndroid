package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.CreateTimesheet
import com.prodacc.data.remote.dao.NewTimesheet
import com.prodacc.data.remote.dao.Timesheet
import com.prodacc.data.repositories.TimeSheetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class TimeSheetsViewModel(
    private val timeSheetRepository: TimeSheetRepository = TimeSheetRepository(),
    signedInUser: SignedInUser = SignedInUser,
    private val jobCardId: String
): ViewModel() {
    private val _timeSheets = MutableStateFlow<List<Timesheet>>(emptyList())
    val timeSheets = _timeSheets.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _newTimeSheetLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newTimeSheetLoadState = _newTimeSheetLoadState.asStateFlow()

    //New TimeSheet
    private val _newTimeSheet = MutableStateFlow<CreateTimesheet>(CreateTimesheet(technicianId = signedInUser.user!!.employeeId, jobCardId = UUID.fromString(jobCardId)))
    val newTimeSheet = _newTimeSheet.asStateFlow()

    init {
        viewModelScope.launch {
            fetchTimeSheets()
        }
    }

    fun onTitleChange(title: String){
        _newTimeSheet.value = _newTimeSheet.value.copy(sheetTitle = title)
    }

    fun onReportChange(report: String){
        _newTimeSheet.value = _newTimeSheet.value.copy(report = report)
    }

    fun clockIn(date: LocalDateTime){
        _newTimeSheet.value = _newTimeSheet.value.copy(clockInDateAndTime = date)
    }

    fun clockOut(date: LocalDateTime){
        _newTimeSheet.value = _newTimeSheet.value.copy(clockOutDateAndTime = date)
    }

    fun saveTimesheet(){
        viewModelScope.launch {
            addTimesheets()
        }
    }







    private suspend fun fetchTimeSheets() {
        _loadingState.value = LoadingState.Loading
        try {
            when (val response = timeSheetRepository.getJobCardTimeSheets(UUID.fromString(jobCardId))){
                is TimeSheetRepository.LoadingResult.Error -> {_loadingState.value = LoadingState.Error(response.message)}
                is TimeSheetRepository.LoadingResult.Message -> {_loadingState.value = LoadingState.Error("Returned Message")}
                is TimeSheetRepository.LoadingResult.Success -> {
                    _timeSheets.value = response.timesheets
                    _loadingState.value = LoadingState.Success
                }
                is TimeSheetRepository.LoadingResult.TimeSheet ->{_loadingState.value = LoadingState.Error("Returned timesheet instead of list")}
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

    private suspend fun addTimesheets(){
        if (_newTimeSheet.value.sheetTitle == null || _newTimeSheet.value.report == null || _newTimeSheet.value.clockInDateAndTime == null){
            _newTimeSheetLoadState.value = LoadingState.Error("Missing required fields")
        } else {
            _newTimeSheetLoadState.value = LoadingState.Loading
            when (val response = timeSheetRepository.addTimeSheet(_newTimeSheet.value.toNewTimesheet())){
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
                is TimeSheetRepository.LoadingResult.TimeSheet -> _newTimeSheetLoadState.value = LoadingState.Success
            }
        }
    }

    fun refreshTimeSheets() {
        viewModelScope.launch {
            fetchTimeSheets()
        }
    }


    open class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message: String): LoadingState()

    }

    fun CreateTimesheet.toNewTimesheet() : NewTimesheet{
        return NewTimesheet (
            sheetTitle = this.sheetTitle ?: "__",
            report = this.report?: "",
            clockInDateAndTime = this.clockInDateAndTime ?: LocalDateTime.now(),
            clockOutDateAndTime = this.clockOutDateAndTime,
            jobCardId = this.jobCardId ?: throw IllegalArgumentException("JobCardId cannot be null") ,
            employeeId = this.technicianId ?: throw IllegalArgumentException("technicianId cannot be null"),
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

