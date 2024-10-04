package com.example.prodacc.ui.search.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository

class SearchViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository()
) {
    private val searchQuery = mutableStateOf("")
    val searchQueryState: State<String> = searchQuery

    private val employees = mutableStateOf(employeeRepository.getEmployees())
    val employeesList: State<List<Employee>> = employees

    private val clients = mutableStateOf(clientRepository.getClientsList())
    val clientsList: State<List<Client>> = clients

    private val vehicles = mutableStateOf(vehicleRepository.getVehicles())
    val vehiclesList: State<List<Vehicle>> = vehicles

    private val jobCards = mutableStateOf(jobCardRepository.generateJobCards(10))
    val jobCardsList: State<List<JobCard>> = jobCards


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