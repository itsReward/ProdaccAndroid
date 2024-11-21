package com.example.prodacc.ui.employees.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.ui.employees.stateClasses.EmployeeDetailsState
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardRepository
import kotlinx.coroutines.launch
import java.util.UUID

class EmployeeDetailsViewModel (
    private val employeeId: String,
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository()
): ViewModel() {
    private val employee = employeeRepository.getEmployee(UUID.fromString(employeeId))
    var jobCards = emptyList<JobCard?>()

    init {
        viewModelScope.launch {
            jobCards = employee.jobCards.map { jobCard -> jobCardRepository.getJobCard(jobCard.id) }
            updateState { copy(jobCards = jobCards) }

        }
    }


    private val _state = mutableStateOf(EmployeeDetailsState( employee = employee, jobCards = jobCards.filterNotNull()))
    var state: State<EmployeeDetailsState> = _state

    fun updateState(update: EmployeeDetailsState.() -> EmployeeDetailsState) {
        _state.value = state.value.update()
    }

    fun getJobCards(){

    }
}