package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
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
) :ViewModel(){

    private val employees = employeeRepository.getEmployees()
    private val clients = clientRepository.getClientsList()
    val vehicles = vehicleRepository.getVehicles()

    private val _state = mutableStateOf(
        NewJobCardState(
            null,
            null,
            null,
            null
        )
    )
    val state = _state.value
    private val _vehicleState : MutableState<Vehicle?> = mutableStateOf(null)
    val vehicleState = _vehicleState.value


    private fun updateState(update: (NewJobCardState) -> NewJobCardState){
        _state.value = update(_state.value)
    }


    fun updateVehicle(vehicleId: UUID){
        val vehicle = vehicleRepository.getVehicleById(vehicleId)
        _vehicleState.value = vehicle
        updateState {
            it.copy(vehicleId = vehicleId)
        }
    }


    fun saveJob(){

    }
}