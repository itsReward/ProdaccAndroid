package com.example.prodacc.ui.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.designsystem.designComponents.EmployeeListCategory
import com.prodacc.data.repositories.EmployeeRepository


class EmployeesViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {
    private val _employees = mutableStateOf(employeeRepository.getEmployees())
    val employees = _employees.value.sortedBy { it.employeeName.first() }.groupBy { it.employeeName.first() }.toSortedMap()
        .map { EmployeeListCategory(name = it.key.toString(), items = it.value) }
}