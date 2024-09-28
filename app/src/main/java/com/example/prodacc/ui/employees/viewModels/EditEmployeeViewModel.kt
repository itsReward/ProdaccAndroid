package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.prodacc.ui.employees.stateClasses.EditEmployeeState
import com.example.prodacc.ui.employees.stateClasses.NewEmployeeState
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.repositories.EmployeeRepository
import java.util.UUID

class EditEmployeeViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val employeeId: String
): ViewModel() {

    private val employee = employeeRepository.getEmployee(UUID.fromString(employeeId))
    private val _state = mutableStateOf(EditEmployeeState(employee = employee))
    val state: State<EditEmployeeState> = _state

    private fun updateState(update: EditEmployeeState.() -> EditEmployeeState) {
        _state.value = _state.value.update()
    }
    private fun updateEmployee(update: Employee.() -> Employee) {
        _state.value = _state.value.copy(employee = _state.value.employee.update())
    }

    fun updateFirstName(firstName: String) {
        updateEmployee { copy(employeeName = firstName) }
    }

    fun updateSurname(surname: String) {
        updateEmployee { copy(employeeSurname = surname) }
    }

    fun updatePhone(phone: String) {
        updateEmployee { copy(phoneNumber = phone) }
    }

    fun updateAddress(address: String) {
        updateEmployee { copy(homeAddress = address) }
    }

    fun updateJobTitle(jobTitle: String) {
        updateEmployee { copy(employeeRole = jobTitle) }
    }

    fun updateDepartment(department: String) {
        updateEmployee { copy(employeeDepartment = department) }
    }


    fun saveEmployee() {

    }
}
