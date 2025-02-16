package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class JobCardComment(
    @SerializedName("commentId") val commentId: UUID,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("jobCardName") val jobCardName: String,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("employeeName") val employeeName: String,
    @SerializedName("comment") val comment: String,
    @SerializedName("commentDate") val commentDate: LocalDateTime
)

data class NewComment(
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("comment") val comment: String,
)

data class UpdateComment(
    @SerializedName("jobCardId") val jobCardId: UUID? = null,
    @SerializedName("employeeId") val employeeId: UUID? = null,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("commentId") val commentId: UUID? = null
)
