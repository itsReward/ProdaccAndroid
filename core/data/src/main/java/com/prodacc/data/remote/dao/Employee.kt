package com.prodacc.data.remote.dao

import java.util.UUID

data class Employee (
    val id : UUID,
    var employeeName: String,
    val employeeSurname: String,
    val rating: Float,
    val employeeRole: String,
    val employeeDepartment: String,
    val phoneNumber: String,
    val homeAddress: String,
    val jobCards: List<EmployeeJobCard>
)

data class EmployeeJobCard(
    val id: UUID,
    val name: String
)