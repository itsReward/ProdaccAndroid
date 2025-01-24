package com.example.prodacc.ui.employees.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.employees.stateClasses.NewEmployeeState
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.NewEmployee
import com.prodacc.data.repositories.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class NewEmployeeViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
): ViewModel() {
    private val _state = MutableStateFlow<NewEmployeeState>(NewEmployeeState())
    val state = _state.asStateFlow()

    private val _loadState = MutableStateFlow<SaveState>(SaveState.Idle)
    val loadState = _loadState.asStateFlow()

    private fun updateState(update: NewEmployeeState.() -> NewEmployeeState) {
        _state.value = _state.value.update()
    }

    fun updateFirstName(firstName: String) {
        updateState { copy(employeeName = firstName) }
    }

    fun updateSurname(surname: String) {
        updateState { copy(employeeSurname = surname) }
    }

    fun updatePhone(phone: String) {
        updateState { copy(phoneNumber = phone) }
    }

    fun updateAddress(address: String) {
        updateState { copy(homeAddress = address) }
    }

    fun updateJobTitle(jobTitle: String) {
        updateState { copy(employeeRole = jobTitle) }
    }

    fun updateDepartment(department: String) {
        updateState { copy(employeeDepartment = department) }
    }

    fun resetLoadState() {
        _loadState.value = SaveState.Idle
    }

    fun saveEmployee() {
        // Coroutine scope for launching background operations
        viewModelScope.launch {
            // Validate input fields before attempting to save
            if (validateEmployeeData()) {
                // Set loading state
                _loadState.value = SaveState.Loading

                try {
                    // Create a new employee object from the current state
                    val newEmployee = NewEmployee(
                        employeeName = state.value.employeeName,
                        employeeSurname = state.value.employeeSurname,
                        phoneNumber = state.value.phoneNumber,
                        homeAddress = state.value.homeAddress,
                        employeeRole = state.value.employeeRole,
                        employeeDepartment = state.value.employeeDepartment
                    )

                    // Perform network call to save employee
                    val result = withContext(Dispatchers.IO) {
                        employeeRepository.createEmployee(newEmployee)
                    }

                    // Handle different result scenarios
                    when (result) {
                        is EmployeeRepository.LoadingResult.Success -> {
                            // Save was successful
                            EventBus.emitEmployeeEvent(EventBus.EmployeeEvent.EmployeeCreated)
                            result.employees?.get(0)
                                ?.let { WebSocketInstance.sendWebSocketMessage("NEW_EMPLOYEE", it.id) }
                            _loadState.value = SaveState.Success
                        }
                        is EmployeeRepository.LoadingResult.Error -> {
                            // Handle specific error scenarios
                            _loadState.value = SaveState.Error(
                                result.message ?: "Failed to save employee"
                            )
                        }
                        is EmployeeRepository.LoadingResult.NetworkError -> {
                            // Specific handling for network-related errors
                            _loadState.value = SaveState.Error(
                                "Network error. Please check your connection."
                            )
                        }
                        else -> {
                            // Catch-all for unexpected scenarios
                            _loadState.value = SaveState.Error(
                                "An unexpected error occurred"
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Comprehensive error handling
                    val errorMessage = when (e) {
                        is CancellationException -> throw e // Rethrow coroutine cancellation
                        is IOException -> "Network connection error"
                        else -> "Unexpected error: ${e.localizedMessage}"
                    }
                    _loadState.value = SaveState.Error(errorMessage)

                    // Log the full error for debugging
                    Log.e("NewEmployeeViewModel", "Save employee error", e)
                }
            }
        }
    }

    // Input validation method
    private fun validateEmployeeData(): Boolean {
        try {
            val currentState = state.value!!
            return when {
                currentState.employeeName?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("First name cannot be empty")
                    false
                }
                currentState.employeeSurname?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("Surname cannot be empty")
                    false
                }
                currentState.phoneNumber?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("Phone number cannot be empty")
                    false
                }
                currentState.homeAddress?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("Home address cannot be empty")
                    false
                }
                currentState.employeeRole?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("Job title cannot be empty")
                    false
                }
                currentState.employeeDepartment?.isBlank() == true -> {
                    _loadState.value = SaveState.Error("Department cannot be empty")
                    false
                }
                else -> true
            }
        } catch (e: Exception){
            _loadState.value = SaveState.Error("An unexpected error occurred")
            return false
        }

    }

    sealed class SaveState{
        data object Idle: SaveState()
        data object Loading: SaveState()
        data object Success: SaveState()
        data class Error(val message: String): SaveState()
    }

}
