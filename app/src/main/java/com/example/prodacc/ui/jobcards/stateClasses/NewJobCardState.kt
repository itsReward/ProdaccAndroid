package com.example.prodacc.ui.jobcards.stateClasses

import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Vehicle
import java.time.LocalDateTime
import java.util.UUID

data class NewJobCardState(
    val vehicleId: UUID?,                      // Mandatory
    val serviceAdvisorId: UUID?,               // Mandatory
    val supervisorId: UUID?,                   // Mandatory
    val dateAndTimeIn: LocalDateTime?,         // Mandatory
    val jobCardStatus: String? = null,        // Optional
    val estimatedTimeOfCompletion: LocalDateTime? = null, // Optional
    val dateAndTimeFrozen: LocalDateTime? = null,        // Optional
    val dateAndTimeClosed: LocalDateTime? = null,        // Optional
    val priority: Boolean? = false,            // Optional
    val jobCardDeadline: LocalDateTime? = null // Optional
)

data class ClientAndVehicleState(
    val client: Client?,
    val vehicle: Vehicle?
)
