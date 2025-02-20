package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.JobCardTechnician
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.JobCardTechnicianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobCardTechnicianViewModel @Inject constructor(
    private val jobCardTechnicianRepository: JobCardTechnicianRepository,
    private val employeeRepository: EmployeeRepository,
    private val webSocketInstance: WebSocketInstance,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener {
    // Get jobCardId from SavedStateHandle
    private val jobCardId: String = checkNotNull(savedStateHandle["jobCardId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    private val _jobCardTechnicians = MutableStateFlow<List<UUID>>(emptyList())

    private val _technicians = MutableStateFlow<List<Employee>>(emptyList())
    val technicians = _technicians.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()


    init {
        webSocketInstance.addWebSocketListener(this)

        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            fetchJobCardTechnicians()
        }
    }


    fun addTechnician(technicianId: UUID) {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                when (val response = jobCardTechnicianRepository.addTechnicianToJobCard(
                    JobCardTechnician(
                        jobCardId = UUID.fromString(jobCardId),
                        technicianId = technicianId
                    )
                )) {
                    is JobCardTechnicianRepository.LoadingResult.Error -> _loadingState.value =
                        LoadingState.Error(response.message)

                    is JobCardTechnicianRepository.LoadingResult.Loading -> _loadingState.value =
                        LoadingState.Loading

                    is JobCardTechnicianRepository.LoadingResult.Success -> {
                        webSocketInstance.sendWebSocketMessage("NEW_JOB_CARD_TECHNICIAN", jobCardId)
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> _loadingState.value = LoadingState.Error("Network Error")
                    else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
                }
            }
        }


    }

    fun removeTechnician(technicianId: UUID) {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                val currentTechnicians = _technicians.value.toMutableList()
                when (val response = jobCardTechnicianRepository.removeTechnician(
                    JobCardTechnician(
                        jobCardId = UUID.fromString(jobCardId),
                        technicianId = technicianId
                    )
                )
                ) {
                    is JobCardTechnicianRepository.LoadingResult.Error -> _loadingState.value =
                        LoadingState.Error(response.message)

                    is JobCardTechnicianRepository.LoadingResult.Loading -> _loadingState.value =
                        LoadingState.Loading

                    is JobCardTechnicianRepository.LoadingResult.Success -> {
                        webSocketInstance.sendWebSocketMessage(
                            "DELETE_JOB_CARD_TECHNICIAN",
                            jobCardId
                        )
                        _loadingState.value = LoadingState.Success
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> _loadingState.value = LoadingState.Error("Network Error")
                    else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }


    private suspend fun fetchJobCardTechnicians() {
        try {
            when (val response =
                jobCardTechnicianRepository.getJobCardTechnicians(UUID.fromString(jobCardId))) {
                is JobCardTechnicianRepository.LoadingResult.Error -> _loadingState.value =
                    LoadingState.Error(response.message)

                is JobCardTechnicianRepository.LoadingResult.Loading -> _loadingState.value =
                    LoadingState.Loading

                is JobCardTechnicianRepository.LoadingResult.Success -> {
                    _jobCardTechnicians.value = response.list
                    fetchTechnicians()
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> _loadingState.value = LoadingState.Error("Network Error")
                else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun fetchTechnicians() {
        try {
            try {
                val tempTechnicians = mutableListOf<Employee>()
                _jobCardTechnicians.value.forEach { technicianId ->
                    try {
                        when (val response = employeeRepository.getEmployee(technicianId)) {
                            is EmployeeRepository.LoadingResult.EmployeeEntity -> {
                                tempTechnicians.add(response.employee)
                            }

                            is EmployeeRepository.LoadingResult.Error -> {
                                _loadingState.value = LoadingState.Error(response.message)
                            }

                            is EmployeeRepository.LoadingResult.NetworkError -> {
                                _loadingState.value = LoadingState.Error("Network Error")
                            }

                            is EmployeeRepository.LoadingResult.Success -> {
                                _loadingState.value =
                                    LoadingState.Error("This wasn't supposed to happen")
                            }
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is IOException -> _loadingState.value =
                                LoadingState.Error("Network Error")

                            else -> _loadingState.value =
                                LoadingState.Error(e.message ?: "Unknown Error")
                        }
                    }
                }
                _technicians.value = tempTechnicians
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "No technicians found")
            }

        } catch (e: Exception) {
            when (e) {
                is IOException -> _loadingState.value = LoadingState.Error("Network Error")
                else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun refreshJobCardTechnicians() {
        _loadingState.value = LoadingState.Loading
        _jobCardTechnicians.value = emptyList()
        _technicians.value = emptyList()
        viewModelScope.launch {
            fetchJobCardTechnicians()
        }
    }

    open class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        viewModelScope.launch {
            when (update) {
                is WebSocketUpdate.NewTechnician -> {
                    if (update.id == UUID.fromString(jobCardId)) {
                        refreshJobCardTechnicians()
                    }
                }

                is WebSocketUpdate.DeleteTechnician -> {
                    if (update.id == UUID.fromString(jobCardId)) {
                        refreshJobCardTechnicians()
                    }
                }

                else -> {}
            }
        }
    }

    override fun onWebSocketError(error: Throwable) {
        _loadingState.value = LoadingState.Error(error.message ?: "WebSocket Error")
    }

}
