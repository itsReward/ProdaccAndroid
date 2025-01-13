package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class Timesheet(
    @SerializedName("id") val id: UUID,
    @SerializedName("sheetTitle") val sheetTitle: String,
    @SerializedName("report") val report: String,
    @SerializedName("clockInDateAndTime") val clockInDateAndTime: LocalDateTime,
    @SerializedName("clockOutDateAndTime") val clockOutDateAndTime: LocalDateTime?,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("jobCardName") val jobCardName: String,
    @SerializedName("technicianId") val technicianId: UUID,
    @SerializedName("technicianName") val technicianName: String
)

data class NewTimesheet(
    @SerializedName("sheetTitle") val sheetTitle: String,
    @SerializedName("report") val report: String,
    @SerializedName("clockInDateAndTime") val clockInDateAndTime: LocalDateTime,
    @SerializedName("clockOutDateAndTime") val clockOutDateAndTime: LocalDateTime?,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("employeeId") val employeeId: UUID
)

data class UpdateTimesheet(
    @SerializedName("sheetTitle") val sheetTitle: String? = null,
    @SerializedName("report") val report: String? = null,
    @SerializedName("clockInDateAndTime") val clockInDateAndTime: LocalDateTime? = null,
    @SerializedName("clockOutDateAndTime") val clockOutDateAndTime: LocalDateTime? = null,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("technicianId") val technicianId: UUID
)

data class CreateTimesheet(
    @SerializedName("sheetTitle") val sheetTitle: String? = null,
    @SerializedName("report") val report: String? = null,
    @SerializedName("clockInDateAndTime") val clockInDateAndTime: LocalDateTime? = null,
    @SerializedName("clockOutDateAndTime") val clockOutDateAndTime: LocalDateTime? = null,
    @SerializedName("jobCardId") val jobCardId: UUID? = null,
    @SerializedName("technicianId") val technicianId: UUID? = null
)