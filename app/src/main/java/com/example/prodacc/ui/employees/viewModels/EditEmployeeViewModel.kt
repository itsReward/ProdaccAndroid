package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prodacc.ui.employees.stateClasses.EditEmployeeState
import com.example.prodacc.ui.employees.stateClasses.NewEmployeeState
import com.prodacc.data.remote.dao.Employee
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

    fun saveEmployee() {

    }

    sealed class LoadState{
        data object Idle: LoadState()
        data object Loading: LoadState()
        data object Success: LoadState()
        data class Error(val message : String) :LoadState()
    }
}
