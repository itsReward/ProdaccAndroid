package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.jobcards.stateClasses.NewJobCardState
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

    val employees = employeeRepository.getEmployees()
    val clients = clientRepository.getClientsList()


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

    fun  updateServiceAdvisor (id: UUID){
        val employee = employeeRepository.getEmployee(id)
        updateState {
            it.copy(serviceAdvisorId = employee.id)
        }
    }





    fun saveJob(){

    }

}