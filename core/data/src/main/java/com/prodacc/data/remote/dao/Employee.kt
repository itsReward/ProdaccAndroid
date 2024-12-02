package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Employee (
    @SerializedName("id") val id : UUID,
    @SerializedName("employeeName") val employeeName: String,
    @SerializedName("employeeSurname") val employeeSurname: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("employeeRole") val employeeRole: String,
    @SerializedName("employeeDepartment") val employeeDepartment: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("homeAddress") val homeAddress: String,
    @SerializedName("jobCards") val jobCards: List<EmployeeJobCard>
)

data class EmployeeJobCard(
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String
)

data class NewEmployee (
    @SerializedName("employeeName") val employeeName: String?,
    @SerializedName("employeeSurname") val employeeSurname: String?,
    @SerializedName("employeeRole") val employeeRole: String?,
    @SerializedName("employeeDepartment") val employeeDepartment: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("homeAddress") val homeAddress: String?
)