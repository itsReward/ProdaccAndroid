package com.example.prodacc.ui.jobcards.viewModels

import com.prodacc.data.remote.dao.JobCard
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

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

    private val _timesheetEvents = MutableSharedFlow<TimesheetEvent>()
    val timesheetEvent = _timesheetEvents.asSharedFlow()

    private val _commentEvents = MutableSharedFlow<CommentEvent>()
    val commentEvent = _commentEvents.asSharedFlow()

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

    suspend fun emitTimeSheetEvent(event: TimesheetEvent){
        _timesheetEvents.emit(event)
    }

    suspend fun emitCommentEvent(event: CommentEvent){
        _commentEvents.emit(event)
    }



    sealed class ClientEvent {
        data object ClientCreated : ClientEvent()
        data object ClientDeleted: ClientEvent()
    }

    sealed class EmployeeEvent {
        data object EmployeeCreated: EmployeeEvent()
        data object EmployeeDeleted: EmployeeEvent()
    }

    sealed class TimesheetEvent {
        data object NewTimesheet: TimesheetEvent()
        data object Error: TimesheetEvent()
    }


    sealed class VehicleEvent{
        data object VehicleCreated:VehicleEvent()
        data object VehicleDeleted:VehicleEvent()
    }

    sealed class JobCardEvent {
        data class StatusChanged(val jobId: UUID) : JobCardEvent()
        data class ReportCRUD(val jobId: UUID) : JobCardEvent()
        data class Error(val message: String) : JobCardEvent()
    }

    sealed class JobCardCRUDEvent {
        data class JobCardCreated(val jobCard : JobCard) : JobCardCRUDEvent()
        data class JobCardDeleted(val jobCardId: UUID) : JobCardCRUDEvent()
    }

    sealed class CommentEvent {
        data object CommentCreated: CommentEvent()
        data object CommentDeleted: CommentEvent()
    }
}