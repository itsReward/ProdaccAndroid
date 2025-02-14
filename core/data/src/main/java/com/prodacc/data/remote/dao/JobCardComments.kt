package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class JobCardComment(
    @SerializedName("commentId") val commentId: UUID,
    @SerializedName("jobCardId") val jobCardId: UUID,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("comment") val comment: String
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
