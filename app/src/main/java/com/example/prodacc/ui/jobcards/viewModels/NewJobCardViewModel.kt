package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.jobcards.stateClasses.NewJobCardState
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.ControlChecklistRepository
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardReportRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardTechnicianRepository
import com.prodacc.data.repositories.ServiceChecklistRepository
import com.prodacc.data.repositories.StateChecklistRepository
import com.prodacc.data.repositories.TimeSheetRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class NewJobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val controlChecklistRepository: ControlChecklistRepository = ControlChecklistRepository(),
    private val serviceChecklist: ServiceChecklistRepository = ServiceChecklistRepository(),
    private val stateChecklist: StateChecklistRepository = StateChecklistRepository(),
    private val timeSheet: TimeSheetRepository = TimeSheetRepository(),
    private val jobCardReportRepository: JobCardReportRepository = JobCardReportRepository(),
    private val jobCardTechnicianRepository: JobCardTechnicianRepository = JobCardTechnicianRepository(),
    private val jobCardReport: JobCardReportRepository = JobCardReportRepository()
) : ViewModel() {

    val clients = emptyList<Client>()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees = _employees.asStateFlow()

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _employeeLoadingState =
        MutableStateFlow<EmployeeLoadingResult>(EmployeeLoadingResult.Idle)
    val employeeLoadingState = _employeeLoadingState.asStateFlow()

    private val _vehiclesLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val vehicleLoadingState = _vehiclesLoadingState.asStateFlow()


    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    private val _supervisor = MutableStateFlow<Employee?>(null)
    val supervisor = _supervisor.asStateFlow()

    private val _serviceAdvisor = MutableStateFlow<Employee?>(null)
    val serviceAdvisor = _serviceAdvisor.asStateFlow()

    private val _saveState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val saveState = _saveState.asStateFlow()

    private val _vehicleLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadState = _vehicleLoadState.asStateFlow()

    private val _serviceAdvisorDropdown = MutableStateFlow<Boolean>(false)
    val serviceAdvisorDropdown = _serviceAdvisorDropdown.asStateFlow()

    private val _supervisorDropdown = MutableStateFlow<Boolean>(false)
    val supervisorDropdown = _supervisorDropdown.asStateFlow()

    val vehiclesDropdown = mutableStateOf(false)

    init {
        viewModelScope.launch {
            getEmployees()
            getVehicles()
        }
    }

    fun toggleSupervisorDropdown() {
        _supervisorDropdown.value = !_supervisorDropdown.value
    }

    fun toggleServiceAdvisor() {
        _serviceAdvisorDropdown.value = !_serviceAdvisorDropdown.value
    }


    private suspend fun getEmployees() {
        _employeeLoadingState.value = EmployeeLoadingResult.Loading
        try {
            when (val response = employeeRepository.getEmployees()) {
                is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                    _employeeLoadingState.value =
                        EmployeeLoadingResult.Error("Returned one element instead of list")
                }

                is EmployeeRepository.LoadingResult.Error -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.Error(response.message)
                }

                EmployeeRepository.LoadingResult.NetworkError -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.NetworkError
                }

                is EmployeeRepository.LoadingResult.Success -> {
                    _employees.value = response.employees ?: emptyList()
                    _employeeLoadingState.value =
                        EmployeeLoadingResult.Success(response.employees ?: emptyList())
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> EmployeeLoadingResult.NetworkError
                else -> EmployeeLoadingResult.Error(e.message ?: "Unknown Error")
            }
        }

    }

    private suspend fun getVehicles() {
        try {
            when (val vehicles = vehicleRepository.getVehicles()) {
                is VehicleRepository.LoadingResult.Error -> {
                    _vehiclesLoadingState.value =
                        LoadingState.Error(vehicles.message ?: "Unknown error")
                }

                is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                    _vehicleLoadState.value = LoadingState.Error(vehicles.message)
                }

                is VehicleRepository.LoadingResult.NetworkError -> {
                    _vehicleLoadState.value = LoadingState.NetworkError
                }

                is VehicleRepository.LoadingResult.SingleEntity -> {
                    //will never happen
                }

                is VehicleRepository.LoadingResult.Success -> {
                    _vehicles.value = vehicles.vehicles ?: emptyList()
                    _vehiclesLoadingState.value = LoadingState.Success(vehicles.vehicles)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> _vehiclesLoadingState.value = LoadingState.NetworkError
                else -> _vehiclesLoadingState.value =
                    LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun getVehicleEntity(id: String) {
        try {
            when (val vehicle = vehicleRepository.getVehicleById(UUID.fromString(id))) {
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    _vehicle.value = vehicle.vehicle
                    _vehicleLoadState.value = LoadingState.Success(vehicle.vehicle!!)
                }

                is VehicleRepository.LoadingResult.Error -> {
                    _vehicleLoadState.value = LoadingState.Error(vehicle.message ?: "Unknown Error")
                }

                is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                    _vehicleLoadState.value = LoadingState.Error(vehicle.message)
                }

                VehicleRepository.LoadingResult.NetworkError -> {
                    _vehicleLoadState.value = LoadingState.Error("Network Error")
                }

                is VehicleRepository.LoadingResult.Success -> {
                    //will never happen for single entity search
                }

                null -> {

                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    _vehicleLoadState.value = LoadingState.Error("Network Error")
                }

                else -> {
                    _vehicleLoadState.value = LoadingState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }




    fun updateServiceAdvisor(employee: Employee) {
        _serviceAdvisor.value = employee
    }

    fun updateSupervisor(employee: Employee) {
        _supervisor.value = employee
    }

    fun updateVehicle(vehicle: Vehicle) {
        _vehicle.value = vehicle
    }



    fun toggleVehiclesDropDown() {
        vehiclesDropdown.value = !vehiclesDropdown.value
    }

    fun saveJob() {
        try {
            when (val response = jobCardRepository.saveJob()) {

            }
        } catch (e: Exception){
            when (e) {
                is IOException -> _saveState.value = LoadingState.NetworkError
                else -> _saveState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    sealed class EmployeeLoadingResult {
        data object Idle : EmployeeLoadingResult()
        data object Loading : EmployeeLoadingResult()
        data class Success(val employees: List<Employee>) : EmployeeLoadingResult()
        data class Error(val message: String) : EmployeeLoadingResult()
        data object NetworkError : EmployeeLoadingResult()

    }

    sealed class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data class Success(val entity: Any) : LoadingState()
        data class Error(val message: String) : LoadingState()
        data object NetworkError : LoadingState()
    }



}


class NewJobCardViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(NewJobCardViewModel::class.java)) {
            return NewJobCardViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}