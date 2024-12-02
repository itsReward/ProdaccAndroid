package com.example.prodacc.ui.employees.stateClasses

data class NewEmployeeState (
    val employeeName: String,
    val employeeSurname: String,
    val rating: Float,
    val employeeRole: String,
    val employeeDepartment: String,
    val phoneNumber: String,
    val homeAddress: String
)
