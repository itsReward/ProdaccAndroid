package com.example.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "control_checklists")
data class ControlChecklist(
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val jobCardId: UUID,
    val technicianId: UUID,
    val created: LocalDateTime,
    val checklist: JSONObject,
    val report: String,
    val version: Int = 0

)
