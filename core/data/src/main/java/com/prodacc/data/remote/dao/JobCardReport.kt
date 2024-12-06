package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class JobCardReport(
    @SerializedName("reportId") val reportId: UUID,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("reportType") val reportType: String,
    @SerializedName("jobReport") val jobReport: String
)

data class NewJobCardReport(
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("reportType") val reportType: String,
    @SerializedName("jobReport") val jobReport: String
)

data class UpdateJobCardReport(
    @SerializedName("jobCardId") val jobCardId: UUID? = null,
    @SerializedName("employeeId") val employeeId: UUID? = null,
    @SerializedName("reportType") val reportType: String? = null,
    @SerializedName("jobReport") val jobReport: String? = null
)
