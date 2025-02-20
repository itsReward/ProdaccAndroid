package com.example.prodacc.ui.employees.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.viewModels.EventBus
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.repositories.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val tokenManager: TokenManager,
    private val signedInUserManager: SignedInUserManager,
    webSocketInstance: WebSocketInstance
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees = _employees.asStateFlow()

    val userRole = signedInUserManager.role

    private val _technicians =
        MutableStateFlow(_employees.value.filter { it.employeeRole == "technician" })
    val technicians = _technicians.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        webSocketInstance.addWebSocketListener(this)

        _loadState.value = LoadState.Loading

        viewModelScope.launch {
            EventBus.employeeEvent.collect { event ->
                when (event) {
                    EventBus.EmployeeEvent.EmployeeCreated -> refreshEmployees()
                    EventBus.EmployeeEvent.EmployeeDeleted -> refreshEmployees()
                }
            }
        }


        viewModelScope.launch {
            getEmployees()
        }
    }

    fun logOut(){
        tokenManager.saveToken(null)
    }

    fun refreshEmployees() {
        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            getEmployees()
        }
    }

    private suspend fun getEmployees() {
        when (val response = employeeRepository.getEmployees()) {
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
                _employees.value = response.employees ?: emptyList()
                _technicians.value = response.employees?.filter { it.employeeRole == "technician" }
                    ?: emptyList()
                _loadState.value = LoadState.Success

            }
        }
    }


    sealed class LoadState {
        data object Idle : LoadState()
        data object Loading : LoadState()
        data object Success : LoadState()
        data class Error(val message: String) : LoadState()

    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when (update) {
            is WebSocketUpdate.NewEmployee -> {
                refreshEmployees()
            }

            is WebSocketUpdate.UpdateEmployee -> {
                refreshEmployees()
            }

            is WebSocketUpdate.DeleteEmployee -> {
                refreshEmployees()
            }

            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        //do nothing
    }
}