package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class ControlChecklist(
    @SerializedName("id") val id: UUID,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("jobCardName") val jobCardName: String,
    @SerializedName("technicianId") val technicianId: UUID,
    @SerializedName("technicianName") val technicianName: String,
    @SerializedName("created") val created: LocalDateTime,
    @SerializedName("checklist") val checklist: Map<String, String>
)

data class NewControlChecklist(
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("technicianId") val technicianId: UUID,
    @SerializedName("created") val created: LocalDateTime,
    @SerializedName("checklist") val checklist: Map<String, String>
)



