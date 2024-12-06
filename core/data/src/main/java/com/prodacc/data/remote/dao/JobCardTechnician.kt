package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class JobCardTechnician(
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("technicianId") val technicianId: UUID
)
