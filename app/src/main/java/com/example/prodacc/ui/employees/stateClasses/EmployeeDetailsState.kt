package com.example.prodacc.ui.employees.stateClasses

import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCard

data class EmployeeDetailsState (
    val employee: Employee,
    val deleteToggle: Boolean = false,
    val editToggle: Boolean = false,
    val jobCards: List<JobCard> = emptyList()
)
