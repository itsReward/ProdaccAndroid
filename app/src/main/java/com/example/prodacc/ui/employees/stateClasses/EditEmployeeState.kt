package com.example.prodacc.ui.employees.stateClasses

import com.prodacc.data.remote.dao.Employee

data class EditEmployeeState(
    val employee: Employee,
    val isLoading: Boolean = false,
    val error: String? = null
)
