package com.prodacc.data.remote

import java.util.UUID

sealed class WebSocketUpdate {
    data class JobCardCreated(val jobCardId: UUID) : WebSocketUpdate()
    data class JobCardDeleted(val jobCardId: UUID) : WebSocketUpdate()
    data class JobCardUpdated(val jobCardId: UUID) : WebSocketUpdate()

    data class StatusChanged(val jobCardId: UUID) : WebSocketUpdate()

    data class NewReport(val id: UUID): WebSocketUpdate()
    data class UpdateReport(val id: UUID): WebSocketUpdate()
    data class DeleteReport(val id: UUID): WebSocketUpdate()

    data class NewTechnician(val id: UUID): WebSocketUpdate()
    data class DeleteTechnician(val id: UUID): WebSocketUpdate()

    data class NewServiceChecklist(val id: UUID): WebSocketUpdate()
    data class UpdateServiceChecklist(val id: UUID): WebSocketUpdate()
    data class DeleteServiceChecklist(val id: UUID): WebSocketUpdate()

    data class NewStateChecklist(val id: UUID): WebSocketUpdate()
    data class UpdateStateChecklist(val id: UUID): WebSocketUpdate()
    data class DeleteStateChecklist(val id: UUID): WebSocketUpdate()

    data class NewControlChecklist(val id: UUID): WebSocketUpdate()
    data class UpdateControlChecklist(val id: UUID): WebSocketUpdate()
    data class DeleteControlChecklist(val id: UUID): WebSocketUpdate()

    data class NewTimesheet(val id: UUID): WebSocketUpdate()
    data class UpdateTimesheet(val id: UUID): WebSocketUpdate()
    data class DeleteTimesheet(val id: UUID): WebSocketUpdate()

    data class NewClient(val id: UUID): WebSocketUpdate()
    data class UpdateClient(val id: UUID): WebSocketUpdate()
    data class DeleteClient(val id: UUID): WebSocketUpdate()

    data class NewVehicle(val id: UUID): WebSocketUpdate()
    data class UpdateVehicle(val id: UUID): WebSocketUpdate()
    data class DeleteVehicle(val id: UUID): WebSocketUpdate()

    data class NewEmployee(val id: UUID): WebSocketUpdate()
    data class UpdateEmployee(val id: UUID): WebSocketUpdate()
    data class DeleteEmployee(val id: UUID): WebSocketUpdate()
}