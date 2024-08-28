package com.prodacc.data.remote.dao

import com.prodacc.data.local.entities.Timesheet
import java.time.LocalDateTime
import java.util.UUID

data class JobCard(
    val id: UUID,
    val jobCardName: String,
    val jobCardNumber: Int,
    val vehicleId: UUID,
    val vehicleName: String,
    val clientId: UUID,
    val clientName: String,
    val serviceAdvisorId: UUID,
    val serviceAdvisorName: String,
    val supervisorId: UUID,
    val supervisorName: String,
    val dateAndTimeIn: LocalDateTime,
    val estimatedTimeOfCompletion: LocalDateTime,
    val dateAndTimeFrozen: LocalDateTime?,
    val dateAndTimeClosed: LocalDateTime?,
    val priority: Boolean,
    val jobCardDeadline: LocalDateTime,
    val timesheets: List<Timesheet>,
    val stateChecklistId: UUID,
    val serviceChecklistId: UUID,
    val controlChecklistId: UUID
)