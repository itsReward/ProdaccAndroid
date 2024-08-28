package com.prodacc.data.local.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Fts4
@Entity(tableName = "job_cards")
data class JobCard (
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val vehicleId: UUID,
    val clientId: UUID,
    val supervisorId: UUID,
    val serviceAdvisor: UUID,
    val technicians: List<UUID> = emptyList(),
    val priority: String,
    val dateIn: LocalDateTime,
    val estimatedTimeOfCompletion: LocalDateTime?,
    val dateFrozen: LocalDateTime?,
    val dateClosed: LocalDateTime?,
    val deadline: LocalDateTime?,
    val serviceAdvisorReport: String,
    val diagnosticsReport: String?,
    val supervisorReport: String?,
    val workDone: String?,
    val additionalWorkDone: String?,
    val status: String,
    val jobCardName: String,
    val jobCardNumber: Int,
    val version: Int = 0

)