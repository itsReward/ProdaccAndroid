package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class JobCardStatus (
    @SerializedName("statusId") val statusId : UUID,
    @SerializedName("jobId") val jobId : UUID,
    @SerializedName("status") val status : String,
    @SerializedName("createdAt") val createdAt : LocalDateTime
)

data class NewJobCardStatus (
    @SerializedName("jobId") val jobId : UUID,
    @SerializedName("status") val status : String,
    @SerializedName("createdAt") val createdAt : LocalDateTime?= null
)