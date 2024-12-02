package com.example.prodacc.ui.employees.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.NewEmployee
import com.prodacc.data.repositories.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class EditEmployeeViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val employeeId: String
): ViewModel() {

    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState = _updateState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchEmployee()
        }
    }

    private fun updateEmployee(update: Employee.() -> Employee) {
        _employee.value = _employee.value?.update()
    }

    fun updateFirstName(firstName: String) {
        updateEmployee { copy(employeeName = firstName) }
    }

    fun updateSurname(surname: String) {
        updateEmployee { copy(employeeSurname = surname) }
    }

    fun updatePhone(phone: String) {
        updateEmployee { copy(phoneNumber = phone) }
    }

    fun updateAddress(address: String) {
        updateEmployee { copy(homeAddress = address) }
    }

    fun updateJobTitle(jobTitle: String) {
        updateEmployee { copy(employeeRole = jobTitle) }
    }

    fun updateDepartment(department: String) {
        updateEmployee { copy(employeeDepartment = department) }
    }


    fun refreshEmployee(){
        viewModelScope.launch {
            fetchEmployee()
        }
    }
    private suspend fun fetchEmployee() {
        try {
            _loadState.value = LoadState.Loading
            val id = UUID.fromString(employeeId)
            if (id is UUID){
                when (val result = employeeRepository.getEmployee(id)) {
                    is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                        _employee.value = result.employee
                        _loadState.value = LoadState.Success
                    }
                    is EmployeeRepository.LoadingResult.Error -> {
                        _loadState.value = LoadState.Error(result.message)
                    }
                    EmployeeRepository.LoadingResult.NetworkError -> {
                        _loadState.value = LoadState.Error("Network Error")
                    }
                    is EmployeeRepository.LoadingResult.Success -> {
                        _loadState.value = LoadState.Error("Returned list instead of one element")
                    }
                }
            } else {
                _loadState.value = LoadState.Error("Invalid ID")
            }


        }catch(e:Exception){
            when (e){
                is IOException -> LoadState.Error("Network Error")
                else -> LoadState.Error(e.message?:"Unknown Error")
            }
        }
    }

    fun updateEmployee() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            try {
                val currentEmployee = _employee.value
                if (currentEmployee != null) {
                    val newEmployee = NewEmployee(
                        employeeName = currentEmployee.employeeName,
                        employeeSurname = currentEmployee.employeeSurname,
                        phoneNumber = currentEmployee.phoneNumber,
                        homeAddress = currentEmployee.homeAddress,
                        employeeRole = currentEmployee.employeeRole,
                        employeeDepartment = currentEmployee.employeeDepartment
                    )

                    val result = employeeRepository.updateEmployee(
                        UUID.fromString(employeeId),
                        newEmployee
                    )

                    when (result) {
                        is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                            _updateState.value = UpdateState.Success
                            _employee.value = result.employee
                        }
                        is EmployeeRepository.LoadingResult.Error -> {
                            _updateState.value = UpdateState.Error(result.message)
                        }
                        EmployeeRepository.LoadingResult.NetworkError -> {
                            _updateState.value = UpdateState.Error("Network Error")
                        }
                        else -> {
                            _updateState.value = UpdateState.Error("Unknown Error")
                        }
                    }
                } else {
                    _updateState.value = UpdateState.Error("No employee data")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "Update failed")
            }
        }
    }

    sealed class UpdateState {
        data object Idle : UpdateState()
        data object Loading : UpdateState()
        data object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    sealed class LoadState{
        data object Idle: LoadState()
        data object Loading: LoadState()
        data object Success: LoadState()
        data class Error(val message : String) :LoadState()
    }
}
