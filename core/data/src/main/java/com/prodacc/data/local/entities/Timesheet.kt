package com.prodacc.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "timesheets")
data class Timesheet(
    @PrimaryKey(autoGenerate = true) val id: UUID,

    @SerializedName("clockIn") val clockIn: LocalDateTime,
    @SerializedName("clockOut") val clockOut: LocalDateTime,
    @SerializedName("technician") val technician: UUID,
    @SerializedName("sheetTitle") val sheetTitle: String,
    @SerializedName("report") val report: String,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("version") val version: Int = 0
)