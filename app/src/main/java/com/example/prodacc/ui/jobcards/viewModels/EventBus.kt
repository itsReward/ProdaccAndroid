package com.example.prodacc.ui.jobcards.viewModels

import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.JobCard
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<JobCardEvent>()
    val events = _events.asSharedFlow()

    private val _crudEvents = MutableSharedFlow<JobCardCRUDEvent>()
    val crudEvents = _crudEvents.asSharedFlow()

    private val _vehicleEvents = MutableSharedFlow<VehicleEvent>()
    val vehicleEvent = _vehicleEvents.asSharedFlow()

    private val _employeeEvents = MutableSharedFlow<EmployeeEvent>()
    val employeeEvent = _employeeEvents.asSharedFlow()

    private val _clientEvents = MutableSharedFlow<ClientEvent>()
    val clientEvent = _clientEvents.asSharedFlow()

    suspend fun emit(event: JobCardEvent) {
        _events.emit(event)
    }

    suspend fun emit(event: JobCardCRUDEvent) {
        _crudEvents.emit(event)
    }

    suspend fun emitVehicleEvents(event: VehicleEvent){
        _vehicleEvents.emit(event)
    }

    suspend fun emitClientEvent(event: ClientEvent){
        _clientEvents.emit(event)
    }

    suspend fun emitEmployeeEvent(event: EmployeeEvent){
        _employeeEvents.emit(event)
    }



    sealed class ClientEvent {
        data object ClientCreated : ClientEvent()
        data object ClientDeleted: ClientEvent()
    }

    sealed class EmployeeEvent {
        data object EmployeeCreated: EmployeeEvent()
        data object EmployeeDeleted: EmployeeEvent()
    }


    sealed class VehicleEvent{
        data object VehicleCreated:VehicleEvent()
        data object VehicleDeleted:VehicleEvent()
    }

    sealed class JobCardEvent {
        data object StatusChanged : JobCardEvent()
        data class Error(val message: String) : JobCardEvent()
    }

    sealed class JobCardCRUDEvent {
        data class JobCardCreated(val jobCard : JobCard) : JobCardCRUDEvent()
        data object JobCardDeleted : JobCardCRUDEvent()

    }
}