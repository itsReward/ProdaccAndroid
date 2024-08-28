package com.prodacc.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "service_checklists")
data class ServiceChecklist(
    @PrimaryKey(autoGenerate = true) val id: UUID,
    val jobCardId: UUID,
    val technicianId: UUID,
    val created: LocalDateTime,
    val checklist: JSONObject,
    val version: Int = 0

)