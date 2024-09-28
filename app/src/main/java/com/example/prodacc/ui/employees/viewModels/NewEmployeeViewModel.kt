package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.prodacc.ui.employees.stateClasses.NewEmployeeState
import com.prodacc.data.repositories.EmployeeRepository

class NewEmployeeViewModel(
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
) {
    private val _state = mutableStateOf(NewEmployeeState())
    val state: State<NewEmployeeState> = _state

    private fun updateState(update: NewEmployeeState.() -> NewEmployeeState) {
        _state.value = _state.value.update()
    }

    fun updateFirstName(firstName: String) {
        updateState { copy(employeeName = firstName) }
    }

    fun updateSurname(surname: String) {
        updateState { copy(employeeSurname = surname) }
    }

    fun updatePhone(phone: String) {
        updateState { copy(phoneNumber = phone) }
    }

    fun updateAddress(address: String) {
        updateState { copy(homeAddress = address) }
    }

    fun updateJobTitle(jobTitle: String) {
        updateState { copy(employeeRole = jobTitle) }
    }

    fun updateDepartment(department: String) {
        updateState { copy(employeeDepartment = department) }
    }


    fun saveEmployee() {

    }

}
