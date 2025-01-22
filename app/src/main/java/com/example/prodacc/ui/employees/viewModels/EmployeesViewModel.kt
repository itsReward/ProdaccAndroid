package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.designsystem.designComponents.EmployeeListCategory
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.example.prodacc.ui.jobcards.viewModels.JobCardDetailsViewModel
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

    private val _technicians = MutableStateFlow(_employees.value.filter { it.employeeRole == "technician" })
    val technicians = _technicians.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        _loadState.value = LoadState.Loading

        viewModelScope.launch {
            EventBus.employeeEvent.collect{ event ->
                when(event){
                    EventBus.EmployeeEvent.EmployeeCreated -> refreshEmployees()
                    EventBus.EmployeeEvent.EmployeeDeleted -> refreshEmployees()
                }
            }
        }


        viewModelScope.launch {
            getEmployees()
        }
    }

    fun refreshEmployees() {
        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            getEmployees()
        }
    }

    private suspend fun getEmployees() {
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
                _employees.value = response.employees?: emptyList()
                _technicians.value = response.employees?.filter { it.employeeRole == "technician" }
                    ?: emptyList()
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

class EmployeesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(EmployeesViewModel::class.java)) {
            return EmployeesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}