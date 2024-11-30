package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designsystem.designComponents.EmployeeListCategory
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.repositories.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EmployeesViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees = _employees.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        viewModelScope.launch {
            getEmployees()
        }
    }

    fun refreshEmployees() {
        viewModelScope.launch {
            getEmployees()
        }
    }

    suspend fun getEmployees() {
        _loadState.value = LoadState.Loading
        when (val response = employeeRepository.getEmployees()){
            is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                _loadState.value = LoadState.Error("returned single entity instead of list")
            }
            is EmployeeRepository.LoadingResult.Error -> {
                _loadState.value = LoadState.Error(response.message)
            }
            is EmployeeRepository.LoadingResult.NetworkError -> {
                _loadState.value = LoadState.Error("Network Error")
            }
            is EmployeeRepository.LoadingResult.Success -> {
                _loadState.value = LoadState.Success
            }
        }
    }


    sealed class LoadState{
        data object Idle : LoadState()
        data object Loading : LoadState()
        data object Success : LoadState()
        data class Error(val message: String) : LoadState()

    }
}