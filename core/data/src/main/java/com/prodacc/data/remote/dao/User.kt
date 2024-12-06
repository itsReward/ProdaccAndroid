package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class User(
    @SerializedName("id") val id: UUID,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("userRole") val userRole: String,
    @SerializedName("employeeId") val employeeId: UUID,
    @SerializedName("employeeName") val employeeName: String,
    @SerializedName("employeeSurname") val employeeSurname: String
)

data class NewUser(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("userRole") val userRole: String,
    @SerializedName("employeeId") val employeeId: UUID
)