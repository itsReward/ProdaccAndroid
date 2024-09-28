package com.example.prodacc.ui.employees.stateClasses

data class NewEmployeeState (
    val employeeName: String? = null,
    val employeeSurname: String? = null,
    val rating: Float? = null,
    val employeeRole: String? = null,
    val employeeDepartment: String? = null,
    val phoneNumber: String? = null,
    val homeAddress: String? = null
)
