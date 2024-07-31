package com.example.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.UUID

@Entity(tableName = "state_checklists")
data class StateChecklist(
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val jobCardId: UUID,
    val millageIn: String,
    val millageOut: String,
    @ColumnInfo(name = "fuel_level_in") val fuelLevelIn: String,
    @ColumnInfo(name = "fuel_level_out") val fuelLevelOut: String,
    val checklist: JSONObject,
    val version: Int = 0

)