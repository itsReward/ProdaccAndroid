package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.util.getColumnIndexOrThrow
import com.example.prodacc.ui.employees.stateClasses.EmployeeDetailsState
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class EmployeeDetailsViewModel (
    private val employeeId: String,
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository()
): ViewModel() {
    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    private val _employeeLoadState = MutableStateFlow<EmployeeLoadState>(EmployeeLoadState.Idle)
    val employeeLoadState = _employeeLoadState.asStateFlow()

    private val _jobCardLoadState = MutableStateFlow<JobCardsLoadState>(JobCardsLoadState.Idle)
    val jobCardLoadState = _jobCardLoadState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _deleteConfirmationState = MutableStateFlow(false)
    val deleteConfirmationState = _deleteConfirmationState.asStateFlow()

    private val _jobCards = MutableStateFlow<List<JobCard?>>(emptyList())
    var jobCards = _jobCards.asStateFlow()

    init {
        viewModelScope.launch {
            getEmployee()
            getJobCards()
        }
    }

    fun refreshEmployee(){
        viewModelScope.launch {
            getEmployee()
        }
    }

    fun toggleDeleteConfirmation(){
        _deleteConfirmationState.value = !_deleteConfirmationState.value
    }
    fun closeDeleteConfirmation(){
        _deleteConfirmationState.value = false
    }

    private suspend fun getEmployee(){
        try {
            _employeeLoadState.value = EmployeeLoadState.Loading
            val id = UUID.fromString(employeeId)
            when (val result = employeeRepository.getEmployee(id)) {
                is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                    _employee.value = result.employee
                    _employeeLoadState.value = EmployeeLoadState.Success
                }
                is EmployeeRepository.LoadingResult.Error -> {
                    _employeeLoadState.value = EmployeeLoadState.Error(result.message)
                }
                is EmployeeRepository.LoadingResult.NetworkError -> {
                    _employeeLoadState.value = EmployeeLoadState.Error("Network Error")
                }
                is EmployeeRepository.LoadingResult.Success -> {
                    _employeeLoadState.value = EmployeeLoadState.Error("Returned List instead of employee")
                }
            }
        }catch (e: Exception){
            when (e) {
                is IOException -> _employeeLoadState.value = EmployeeLoadState.Error("Network Error")
                else -> _employeeLoadState.value = EmployeeLoadState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun getJobCard(id : UUID){
        val result = jobCardRepository.getJobCard(id)
        if (result != null)
            _jobCards.value += result
        else
            _jobCards.value = emptyList()
    }

    private suspend fun getJobCards(){
        if (employee.value != null){
            employee.value!!.jobCards.forEach {
                getJobCard(it.id)
            }
        }
    }

    fun deleteEmployee() {
        viewModelScope.launch {
            _deleteConfirmationState.value = false
            _deleteState.value = DeleteState.Loading
            try {
                val result = employeeRepository.deleteEmployee(UUID.fromString(employeeId))
                when (result) {
                    EmployeeRepository.LoadingResult.Success() -> {
                        _deleteState.value = DeleteState.Success
                    }
                    is EmployeeRepository.LoadingResult.Error -> {
                        _deleteState.value = DeleteState.Error(result.message)
                    }
                    EmployeeRepository.LoadingResult.NetworkError -> {
                        _deleteState.value = DeleteState.Error("Network Error")
                    }
                    else -> {
                        _deleteState.value = DeleteState.Error("Unknown Error")
                    }
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.message ?: "Delete failed")
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }

    sealed class JobCardsLoadState{
        data object Idle : JobCardsLoadState()
        data object Loading : JobCardsLoadState()
        data object Success : JobCardsLoadState()
        data class Error(val message: String) : JobCardsLoadState()

    }

    sealed class EmployeeLoadState{
        data object Idle : EmployeeLoadState()
        data object Loading : EmployeeLoadState()
        data object Success : EmployeeLoadState()
        data class Error(val message: String) : EmployeeLoadState()

    }

    sealed class DeleteState {
        data object Idle : DeleteState()
        data object Loading : DeleteState()
        data object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

}

class EmployeeDetailsViewModelFactory(private val employeeId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(EmployeeDetailsViewModel::class.java)) {
            return EmployeeDetailsViewModel(employeeId = employeeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}