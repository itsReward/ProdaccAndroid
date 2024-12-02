package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import com.prodacc.data.local.entities.Timesheet
import java.time.LocalDateTime
import java.util.UUID

data class JobCard(
    @SerializedName("id") val id: UUID,

    @SerializedName("jobCardName") val jobCardName: String,

    @SerializedName("jobCardNumber") val jobCardNumber: Int,

    @SerializedName("vehicleId") val vehicleId: UUID,

    @SerializedName("vehicleName") val vehicleName: String,

    @SerializedName("clientId") val clientId: UUID,

    @SerializedName("clientName") val clientName: String,

    @SerializedName("serviceAdvisorId") val serviceAdvisorId: UUID,

    @SerializedName("serviceAdvisorName") val serviceAdvisorName: String,

    @SerializedName("supervisorId") val supervisorId: UUID,

    @SerializedName("supervisorName") val supervisorName: String,

    @SerializedName("dateAndTimeIn") val dateAndTimeIn: LocalDateTime? = null,

    @SerializedName("estimatedTimeOfCompletion") val estimatedTimeOfCompletion: LocalDateTime? = null,

    @SerializedName("dateAndTimeFrozen") val dateAndTimeFrozen: LocalDateTime? = null,

    @SerializedName("dateAndTimeClosed") val dateAndTimeClosed: LocalDateTime? = null,

    @SerializedName("priority") val priority: Boolean,

    @SerializedName("jobCardDeadline") val jobCardDeadline: LocalDateTime? = null,

    @SerializedName("timesheets") val timesheets: List<Timesheet>,

    @SerializedName("stateChecklistId") val stateChecklistId: UUID,

    @SerializedName("serviceChecklistId") val serviceChecklistId: UUID,

    @SerializedName("controlChecklistId") val controlChecklistId: UUID
)
