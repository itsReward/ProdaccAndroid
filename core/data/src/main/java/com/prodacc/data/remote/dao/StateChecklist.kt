package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class StateChecklist(
    @SerializedName("id") val id: UUID,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("jobCardName") val jobCardName: String,
    @SerializedName("millageIn") val millageIn: String,
    @SerializedName("millageOut") val millageOut: String,
    @SerializedName("fuelLevelIn") val fuelLevelIn: String,
    @SerializedName("fuelLevelOut") val fuelLevelOut: String,
    @SerializedName("created") val created: LocalDateTime,
    @SerializedName("checklist") val checklist: Map<String, String>
)

data class NewStateChecklist(
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("millageIn") val millageIn: String,
    @SerializedName("millageOut") val millageOut: String,
    @SerializedName("fuelLevelIn") val fuelLevelIn: String,
    @SerializedName("fuelLevelOut") val fuelLevelOut: String,
    @SerializedName("created") val created: LocalDateTime,
    @SerializedName("checklist") val checklist: Map<String, String>
)
