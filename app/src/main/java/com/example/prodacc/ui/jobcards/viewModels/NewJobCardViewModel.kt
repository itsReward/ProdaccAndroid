package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.stateClasses.NewJobCardState
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
    private val jobCardReport: JobCardReportRepository = JobCardReportRepository(),
    vehicleId: String
) :ViewModel(){

    val clients = clientRepository.getClientsList()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees = _employees.asStateFlow()
    private val _employeeLoadingState = MutableStateFlow<EmployeeLoadingResult>(EmployeeLoadingResult.Idle)
    val employeeLoadingState = _employeeLoadingState.asStateFlow()




    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    val serviceAdvisorDropDown = mutableStateOf(false)
    val supervisor = mutableStateOf(false)
    val technicians = mutableStateOf(false)

    init {
        viewModelScope.launch {
            getVehicleEntity(vehicleId)
        }
    }

    private val _state = mutableStateOf(
        NewJobCardState(
            vehicle.value?.id,
            null,
            null,
            null
        )
    )
    val state = _state.value
    private val _vehicleState : MutableState<Vehicle?> = mutableStateOf(null)
    val vehicleState = _vehicleState.value

    suspend fun getEmployees(){
        _employeeLoadingState.value = EmployeeLoadingResult.Loading
        try {
            when (val response = employeeRepository.getEmployees()){
                is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.Error("Returned one element instead of list")
                }
                is EmployeeRepository.LoadingResult.Error -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.Error(response.message)
                }
                EmployeeRepository.LoadingResult.NetworkError -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.NetworkError
                }
                is EmployeeRepository.LoadingResult.Success -> {
                    _employeeLoadingState.value = EmployeeLoadingResult.Success(response.employees)
                }
            }
        } catch (e: Exception){
            when (e){
                is IOException -> EmployeeLoadingResult.NetworkError
                else -> EmployeeLoadingResult.Error(e.message?:"Unknown Error")
            }
        }

    }

    suspend fun getVehicleEntity(id: String){
        try {
            val vehicle = vehicleRepository.getVehicleById(UUID.fromString(id))
            when (vehicle) {
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    _vehicleState.value = vehicle.vehicle
                    _vehicle.value = vehicle.vehicle
                }
                is VehicleRepository.LoadingResult.Error -> TODO()
                is VehicleRepository.LoadingResult.ErrorSingleMessage -> TODO()
                VehicleRepository.LoadingResult.NetworkError -> TODO()
                is VehicleRepository.LoadingResult.Success -> TODO()
                null -> TODO()
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> TODO()
                else -> TODO()
            }
        }
    }


    private fun updateState(update: (NewJobCardState) -> NewJobCardState){
        _state.value = update(_state.value)
    }

    fun  updateServiceAdvisor (employee: Employee){
        updateState {
            it.copy(serviceAdvisorId = employee.id)
        }
    }

    fun  updateSupervisor (employee: Employee){
        updateState {
            it.copy(supervisorId = employee.id)
        }
    }





    fun saveJob(){

    }

    sealed class EmployeeLoadingResult {
        data object Idle: EmployeeLoadingResult()
        data object Loading: EmployeeLoadingResult()
        data class Success(val employees: List<Employee>) : EmployeeLoadingResult()
        data class Error(val message: String) : EmployeeLoadingResult()
        data object NetworkError : EmployeeLoadingResult()

    }

}