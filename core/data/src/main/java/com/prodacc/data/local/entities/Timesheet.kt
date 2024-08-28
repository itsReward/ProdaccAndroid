package com.prodacc.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "timesheets")
data class Timesheet(
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val clockIn: LocalDateTime,
    val clockOut: LocalDateTime,
    val technician: UUID,
    val sheetTitle: String,
    val report: String,
    val jobCardId: UUID,
    val version: Int = 0
)