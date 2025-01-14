package com.example.prodacc.ui.search.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.jobcards.viewModels.LoadingState
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val searchScreen: String
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    val searchQueryState = searchQuery.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val employees = MutableStateFlow<List<Employee>>(emptyList())
    val employeesList = employees.asStateFlow()

    private val clients = MutableStateFlow<List<Client>>(emptyList())
    val clientsList = clients.asStateFlow()

    private val vehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(emptyList())
    val vehiclesList = vehicles.asStateFlow()

    private val jobCards = MutableStateFlow(emptyList<JobCard>())
    val jobCardsList = jobCards.asStateFlow()

    private val _filteredJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val filteredJobCards = _filteredJobCards.asStateFlow()

    private val _filteredClients = MutableStateFlow<List<Client>>(emptyList())
    val filteredClients = _filteredClients.asStateFlow()

    private val _filteredEmployees = MutableStateFlow<List<Employee>>(emptyList())
    val filteredEmployee = _filteredEmployees.asStateFlow()

    private val _filteredVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val filteredVehicles = _filteredVehicles.asStateFlow()

    init {
        viewModelScope.launch {
            searchData()
        }
    }

    private suspend fun searchData() {
        when (searchScreen) {
            "Job Cards" -> fetchJobCards()
            "Clients" -> fetchClients()
            "Vehicles" -> fetchVehicles()
            "Employees" -> fetchEmployees()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                searchData()
            } finally {
                _refreshing.value = false
            }

        }
    }

    private suspend fun fetchEmployees() {
        try {
            when (val result = employeeRepository.getEmployees()) {
                is EmployeeRepository.LoadingResult.EmployeeEntity -> _loadingState.value =
                    LoadingState.Error("Returned one entity instead of list")

                is EmployeeRepository.LoadingResult.Error -> _loadingState.value =
                    LoadingState.Error(result.message)

                is EmployeeRepository.LoadingResult.NetworkError -> LoadingState.Error("Network Error")
                is EmployeeRepository.LoadingResult.Success -> {
                    employees.value = result.employees ?: emptyList()
                    _filteredEmployees.value = employees.value
                    _loadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }

    }

    private suspend fun fetchVehicles() {
        try {
            when (val response = vehicleRepository.getVehicles()) {
                is VehicleRepository.LoadingResult.Error -> LoadingState.Error(
                    response.message ?: "Unknown Error"
                )

                is VehicleRepository.LoadingResult.ErrorSingleMessage -> LoadingState.Error(response.message)
                is VehicleRepository.LoadingResult.NetworkError -> LoadingState.Error("Network Error")
                is VehicleRepository.LoadingResult.SingleEntity -> _loadingState.value =
                    LoadingState.Error("Returned one entity instead of list")

                is VehicleRepository.LoadingResult.Success -> {
                    vehicles.value = response.vehicles
                    _filteredVehicles.value = vehicles.value
                    _loadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private suspend fun fetchClients() {
        try {
            when (val response = clientRepository.getClients()) {
                is ClientRepository.LoadingResult.Error -> LoadingState.Error(
                    response.message ?: "Unknown Error"
                )

                is ClientRepository.LoadingResult.ErrorSingleMessage -> LoadingState.Error(response.message)
                is ClientRepository.LoadingResult.NetworkError -> LoadingState.Error("Network Error")
                is ClientRepository.LoadingResult.SingleEntity -> _loadingState.value =
                    LoadingState.Error("Returned one entity instead of list")

                is ClientRepository.LoadingResult.Success -> {
                    clients.value = response.clients
                    _filteredClients.value = clients.value
                    _loadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private suspend fun fetchJobCards() {
        try {
            when (val response = jobCardRepository.getJobCards()) {
                is JobCardRepository.LoadingResult.Error -> LoadingState.Error(response.message)
                is JobCardRepository.LoadingResult.ErrorSingleMessage -> LoadingState.Error(response.message)
                is JobCardRepository.LoadingResult.NetworkError -> LoadingState.Error("Network Error")
                is JobCardRepository.LoadingResult.SingleEntity -> _loadingState.value =
                    LoadingState.Error("Returned one entity instead of list")

                is JobCardRepository.LoadingResult.Success -> {
                    jobCards.value = response.jobCards
                    _filteredJobCards.value = jobCards.value
                    _loadingState.value = LoadingState.Success
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }


    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        filterData()
    }

    private fun filterData() {
        when (searchScreen) {
            "Job Cards" -> searchJobCards()
            "Clients" -> searchClients()
            "Vehicles" -> searchVehicles()
            "Employees" -> searchEmployees()
        }
    }

    private fun searchEmployees() {
        if (searchQuery.value == ""){
            _filteredEmployees.value = employees.value
        } else _filteredEmployees.value = employees.value.filter {
            it.employeeName.contains(searchQuery.value, ignoreCase = true)
                    || it.employeeSurname.contains(searchQuery.value, ignoreCase = true)
                    || it.employeeRole.contains(searchQuery.value, ignoreCase = true)
        }

    }

    private fun searchVehicles() {
        if (searchQuery.value == ""){
            _filteredVehicles.value = vehicles.value
        } else _filteredVehicles.value = vehicles.value.filter {
            it.regNumber.contains(searchQuery.value, ignoreCase = true)
                    || it.clientName.contains(searchQuery.value, ignoreCase = true)
                    || it.clientSurname.contains(searchQuery.value, ignoreCase = true)
        }
    }

    private fun searchClients() {
        if (searchQuery.value == ""){
            _filteredClients.value = clients.value
        } else _filteredClients.value = clients.value.filter {
            it.clientName.contains(searchQuery.value, ignoreCase = true)
                    || it.clientSurname.contains(searchQuery.value, ignoreCase = true) }
    }

    private fun searchJobCards() {
        if (searchQuery.value == ""){
            _filteredJobCards.value = jobCards.value
        } else _filteredJobCards.value = jobCards.value.filter {
            it.jobCardName.contains(searchQuery.value, ignoreCase = true)
        }
    }


    private fun handleError(e: Exception) {
        when (e) {
            is IOException -> _loadingState.value = LoadingState.Error("Network Error")
            else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
        }
    }

    sealed class LoadingState {
        data object Loading : LoadingState()
        data object Idle : LoadingState()
        data class Error(val message: String) : LoadingState()
        data object Success : LoadingState()
    }

    sealed class SearchingState {
        data object Searching : SearchingState()
        data object Success : SearchingState()
        data class Error(val message: String) : SearchingState()
    }


}

class SearchViewModelFactory(private val searchScreen: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>, extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchScreen = searchScreen) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}