package com.example.prodacc.ui.employees.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardTechnicianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmployeeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val employeeRepository: EmployeeRepository,
    private val jobCardRepository: JobCardRepository,
    private val jobCardTechnicianRepository: JobCardTechnicianRepository,
    private val signedInUserManager: SignedInUserManager,
    private val webSocketInstance: WebSocketInstance
): ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get employeeId from SavedStateHandle
    private val employeeId: String = checkNotNull(savedStateHandle["employeeId"]) {
        "employeeId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    val userRole = signedInUserManager.role

    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    private val _employeeLoadState = MutableStateFlow<EmployeeLoadState>(EmployeeLoadState.Idle)
    val employeeLoadState = _employeeLoadState.asStateFlow()

    private val _jobCardLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val jobCardLoadState = _jobCardLoadState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _deleteConfirmationState = MutableStateFlow(false)
    val deleteConfirmationState = _deleteConfirmationState.asStateFlow()

    private val _jobCards = MutableStateFlow<List<JobCard?>>(emptyList())
    var jobCards = _jobCards.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)

        _jobCardLoadState.value = LoadingState.Loading
        _employeeLoadState.value = EmployeeLoadState.Loading
        viewModelScope.launch {
            getEmployee()
            getJobCards()
        }
    }

    fun refreshEmployee(){
        _employeeLoadState.value = EmployeeLoadState.Loading
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
            val id = UUID.fromString(employeeId)
            when (val result = employeeRepository.getEmployee(id)) {
                is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                    _employee.value = result.employee
                    getJobCards()
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

    private suspend fun getJobCards() {
        _jobCards.value = emptyList()
        when(_employee.value!!.employeeRole){
            "technician" -> {
                when(val jobCardIds = jobCardTechnicianRepository.getJobCardsForTechnician(
                    _employee.value!!.id)){
                    is JobCardTechnicianRepository.LoadingResult.Error ->  _jobCardLoadState.value = LoadingState.Error(jobCardIds.message)
                    is JobCardTechnicianRepository.LoadingResult.Loading ->  _jobCardLoadState.value = LoadingState.Loading
                    is JobCardTechnicianRepository.LoadingResult.Success -> {
                        try {
                            jobCardIds.list.forEach {
                                when(val response = jobCardRepository.getJobCard(it)){
                                    is JobCardRepository.LoadingResult.Error -> _jobCardLoadState.value = LoadingState.Error(response.message)
                                    is JobCardRepository.LoadingResult.ErrorSingleMessage ->  _jobCardLoadState.value = LoadingState.Error(response.message)
                                    is JobCardRepository.LoadingResult.NetworkError ->  _jobCardLoadState.value = LoadingState.Error("Network Error")
                                    is JobCardRepository.LoadingResult.SingleEntity -> {
                                        _jobCards.value+=response.jobCard
                                    }
                                    is JobCardRepository.LoadingResult.Success -> {/*Will never happen for a single entity search*/}
                                }
                            }
                        } finally {
                            _jobCardLoadState.value = LoadingState.Success
                        }

                    }
                }
            }
            else -> {
                try {
                    val jobCardsList = mutableListOf<JobCard>()
                    _employee.value!!.jobCards.forEach {
                        when(val response = jobCardRepository.getJobCard(it.id)){
                            is JobCardRepository.LoadingResult.Error -> _jobCardLoadState.value = LoadingState.Error(response.message)
                            is JobCardRepository.LoadingResult.ErrorSingleMessage ->  _jobCardLoadState.value = LoadingState.Error(response.message)
                            is JobCardRepository.LoadingResult.NetworkError ->  _jobCardLoadState.value = LoadingState.Error("Network Error")
                            is JobCardRepository.LoadingResult.SingleEntity -> {
                                jobCardsList.add(response.jobCard)
                            }
                            is JobCardRepository.LoadingResult.Success -> {/*Will never happen for a single entity search*/}
                        }
                    }
                    _jobCards.value = jobCardsList

                } catch (e: Exception) {
                    handleError(e)
                } finally {
                    _jobCardLoadState.value = LoadingState.Success
                }
            }
        }
    }

    fun deleteEmployee() {
        viewModelScope.launch {
            _deleteConfirmationState.value = false
            _deleteState.value = DeleteState.Loading
            try {
                when (val result = employeeRepository.deleteEmployee(UUID.fromString(employeeId))) {
                    EmployeeRepository.LoadingResult.Success() -> {
                        EventBus.emitEmployeeEvent(EventBus.EmployeeEvent.EmployeeDeleted)
                        webSocketInstance.sendWebSocketMessage("DELETE_EMPLOYEE", UUID.fromString(employeeId))
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

    private fun handleError(e: Exception) {
        when (e) {
            is IOException -> _jobCardLoadState.value = LoadingState.Error("Network Error")
            else -> _jobCardLoadState.value = LoadingState.Error(e.message ?: "Unknown Error")
        }
    }

    fun refreshJobCards() {
        _jobCardLoadState.value = LoadingState.Loading
        viewModelScope.launch {
            getJobCards()
        }
    }

    sealed class LoadingState{
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()

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

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.UpdateEmployee -> {
                if(update.id == UUID.fromString(employeeId)){
                    refreshEmployee()
                }
            }
            is WebSocketUpdate.DeleteEmployee -> {
                if(update.id == UUID.fromString(employeeId)){
                    _employeeLoadState.value = EmployeeLoadState.Error("Employee deleted")
                }
            }
            else -> {
                // Ignore
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {

    }

}
