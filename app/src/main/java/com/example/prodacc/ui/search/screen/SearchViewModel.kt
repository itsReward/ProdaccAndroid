package com.example.prodacc.ui.search.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class SearchViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository()
): ViewModel() {
    private val searchQuery = mutableStateOf("")
    val searchQueryState: State<String> = searchQuery

    private val employees = MutableStateFlow<List<Employee>>(emptyList())
    val employeesList = employees.asStateFlow()

    private val clients = mutableStateOf(clientRepository.getClientsList())
    val clientsList: State<List<Client>> = clients

    private val vehicles: MutableState<List<Vehicle>> = mutableStateOf(emptyList())
    val vehiclesList: State<List<Vehicle>> = vehicles

    private val jobCards = mutableStateOf(emptyList<JobCard>())
    val jobCardsList: State<List<JobCard>> = jobCards

    init {
        viewModelScope.launch {
            fetchEmployees()
            clients.value = clientRepository.getClientsList()
            //vehicles.value = vehicleRepository.getVehicles()
            //jobCards.value = jobCardRepository.getJobCards()
        }
    }

    suspend fun fetchEmployees() {
        val result = employeeRepository.getEmployees()
        when (result) {
            is EmployeeRepository.LoadingResult.EmployeeEntity -> TODO()
            is EmployeeRepository.LoadingResult.Error -> TODO()
            is EmployeeRepository.LoadingResult.NetworkError -> TODO()
            is EmployeeRepository.LoadingResult.Success -> {
                employees.value = result.employees ?: emptyList()
            }
        }
    }


    private fun updateSearchQuery(query: String){
        searchQuery.value = query
    }

    fun searchEmployees(query: String) {
        updateSearchQuery(query)
        employees.value = employees.value.filter { it.employeeName.contains(query, ignoreCase = true) || it.employeeSurname.contains(query, ignoreCase = true) }
    }

    fun searchClients(query: String) {
        updateSearchQuery(query)
        clients.value = clients.value.filter { it.clientName.contains(query, ignoreCase = true) || it.clientSurname.contains(query, ignoreCase = true) }
    }

    fun searchVehicles(query: String) {
        updateSearchQuery(query)
        vehicles.value = vehicles.value.filter { it.regNumber.contains(query, ignoreCase = true) || it.chassisNumber.contains(query, ignoreCase = true) }
    }

    fun searchJobCards(query: String) {
        updateSearchQuery(query)
        jobCards.value = jobCards.value.filter { it.jobCardName.contains(query, ignoreCase = true) }
    }


}