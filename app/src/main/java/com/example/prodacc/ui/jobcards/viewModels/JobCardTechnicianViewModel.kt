package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCardTechnician
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardTechnicianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class JobCardTechnicianViewModel(
    private val jobCardTechnicianRepository: JobCardTechnicianRepository = JobCardTechnicianRepository(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(),
    private val jobCardId: String
): ViewModel() {
    private val _jobCardTechnicians = MutableStateFlow<List<UUID>>(emptyList())
    private val jobCardTechnicians = _jobCardTechnicians.asStateFlow()

    private val _technicians = MutableStateFlow<List<Employee>>(emptyList())
    val technicians = _technicians.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _addingTechnicianState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val addingTechnicianState = _addingTechnicianState.asStateFlow()

    init{
        viewModelScope.launch {
            fetchJobCardTechnicians()
        }
    }


    fun addTechnician(technicianId: UUID){
        viewModelScope.launch {
            _addingTechnicianState.value = LoadingState.Loading
            try {
                when (val response = jobCardTechnicianRepository.addTechnicianToJobCard(
                    JobCardTechnician(jobCardId = UUID.fromString(jobCardId), technicianId = technicianId)
                )){
                    is JobCardTechnicianRepository.LoadingResult.Error -> _addingTechnicianState.value = LoadingState.Error(response.message)
                    is JobCardTechnicianRepository.LoadingResult.Loading -> _addingTechnicianState.value = LoadingState.Loading
                    is JobCardTechnicianRepository.LoadingResult.Success -> {
                        _technicians.value = emptyList()
                        fetchTechnicians()
                    }
                }
            } catch (e: Exception) {
                when (e){
                    is IOException ->_loadingState.value = LoadingState.Error("Network Error")
                    else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
                }
            }
        }


    }


    private suspend fun fetchJobCardTechnicians() {
        _loadingState.value = LoadingState.Loading
        try {
            when (val response = jobCardTechnicianRepository.getJobCardTechnicians(UUID.fromString(jobCardId))){
                is JobCardTechnicianRepository.LoadingResult.Error -> _loadingState.value = LoadingState.Error(response.message)
                is JobCardTechnicianRepository.LoadingResult.Loading -> _loadingState.value = LoadingState.Loading
                is JobCardTechnicianRepository.LoadingResult.Success -> {
                    _jobCardTechnicians.value = response.list
                    fetchTechnicians()
                }
            }
        } catch (e: Exception) {
            when (e){
                is IOException ->_loadingState.value = LoadingState.Error("Network Error")
                else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun fetchTechnicians() {
        try {
            try{
                _jobCardTechnicians.value.forEach { technicianId ->
                    try {
                        when (val response = employeeRepository.getEmployee(technicianId)){
                            is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                                _technicians.value += response.employee
                            }
                            is EmployeeRepository.LoadingResult.Error -> {
                                _loadingState.value = LoadingState.Error(response.message)
                            }
                            is EmployeeRepository.LoadingResult.NetworkError -> {
                                _loadingState.value = LoadingState.Error("Network Error")
                            }
                            is EmployeeRepository.LoadingResult.Success -> {
                                _loadingState.value = LoadingState.Error("This wasn't supposed to happen")
                            }
                        }
                    } catch (e: Exception) {
                        when (e){
                            is IOException ->_loadingState.value = LoadingState.Error("Network Error")
                            else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
                        }
                    }
                }
                println(technicians)
                _loadingState.value = LoadingState.Success
            } catch (e:Exception) {
                _loadingState.value = LoadingState.Error(e.message?:"No technicians found")
            }

        } catch (e: Exception) {
            when (e){
                is IOException ->_loadingState.value = LoadingState.Error("Network Error")
                else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    open class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message: String): LoadingState()
    }

}

class JobCardTechnicianViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(JobCardTechnicianViewModel::class.java)) {
            return JobCardTechnicianViewModel(jobCardId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}