package com.example.prodacc.ui.vehicles.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class VehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val clientsRepository: ClientRepository = ClientRepository(),
    private val vehicleId: String
) : ViewModel() {
    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    private val _vehicleJobCards = MutableStateFlow<JobCardsLoadState>(JobCardsLoadState.Idle)
    val vehicleJobCards = _vehicleJobCards.asStateFlow()

    private val _loadState = MutableStateFlow<VehicleLoadState>(VehicleLoadState.Idle)
    val loadState = _loadState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteVehicleState>(DeleteVehicleState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _deleteConfirmation = MutableStateFlow(false)
    val deleteConfirmation = _deleteConfirmation.asStateFlow()

    init {
        viewModelScope.launch {
            fetchVehicle()
            fetchJobCards()
        }
    }

    fun toggleDeleteConfirmation() {
        _deleteConfirmation.value = true
    }

    fun resetDeleteConfirmation() {
        _deleteConfirmation.value = false
    }


    fun refreshVehicle() {
        viewModelScope.launch {
            fetchVehicle()
            fetchJobCards()
        }
    }

    fun refreshJobCards() {
        viewModelScope.launch {
            fetchJobCards()
        }
    }

    private suspend fun fetchVehicle() {
        _loadState.value = VehicleLoadState.Loading
        val id: Any = try {
            UUID.fromString(vehicleId)
        } catch (e: IllegalArgumentException) {
            _loadState.value = VehicleLoadState.Error("Invalid UUID format")
        }
        if (id is UUID) {
            when (val vehicle = vehicleRepository.getVehicleById(id)) {
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    if (vehicle.vehicle == null) {
                        _loadState.value = VehicleLoadState.Error(vehicle.error ?: "Returned null")
                    } else {
                        _vehicle.value = vehicle.vehicle
                        _loadState.value = VehicleLoadState.Success
                    }
                }

                is VehicleRepository.LoadingResult.Error -> {
                    _loadState.value = VehicleLoadState.Error(vehicle.message ?: "Loading Error")
                }

                is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                    _loadState.value = VehicleLoadState.Error(vehicle.message)
                }

                is VehicleRepository.LoadingResult.NetworkError -> {
                    _loadState.value = VehicleLoadState.Error("Network Error")
                }

                else -> {
                    _loadState.value = VehicleLoadState.Error("Unknown Error")
                }
            }
        }

    }

    private suspend fun fetchJobCards() {
        try {
            _vehicleJobCards.value = JobCardsLoadState.Loading
            when (val response = jobCardRepository.getJobCards()) {
                is JobCardRepository.LoadingResult.Error -> {
                    _vehicleJobCards.value = JobCardsLoadState.Error(response.message)
                }

                is JobCardRepository.LoadingResult.ErrorSingleMessage -> {
                    _vehicleJobCards.value = JobCardsLoadState.Error(response.message)
                }

                JobCardRepository.LoadingResult.NetworkError -> {
                    _vehicleJobCards.value = JobCardsLoadState.Error("Network Error")
                }

                is JobCardRepository.LoadingResult.Success -> {
                    _vehicleJobCards.value = JobCardsLoadState.Success(
                        response.jobCards.filter {
                            it.vehicleId == UUID.fromString(vehicleId)
                        }.sortedByDescending { it.dateAndTimeClosed }
                    )
                }

                is JobCardRepository.LoadingResult.SingleEntity -> {
                    //will never happen when searching multiple jobCards
                }
            }

        } catch (e: Exception) {
            _vehicleJobCards.value = JobCardsLoadState.Error(e.message ?: "Unknown Error")
        }
    }

    fun deleteVehicle() {
        _deleteConfirmation.value = false
        viewModelScope.launch {
            try {
                _deleteState.value = DeleteVehicleState.Loading
                when (val response = vehicleRepository.deleteVehicle(UUID.fromString(vehicleId))) {
                    is VehicleRepository.LoadingResult.Error -> {
                        _deleteState.value =
                            DeleteVehicleState.Error(response.message ?: "Unknown Error")
                    }

                    is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                        _deleteState.value = DeleteVehicleState.Error(response.message)
                    }

                    is VehicleRepository.LoadingResult.NetworkError -> {
                        _deleteState.value = DeleteVehicleState.Error("Network Error")
                    }

                    is VehicleRepository.LoadingResult.SingleEntity -> {
                        _deleteState.value = DeleteVehicleState.Error("Unknown Error")
                    }

                    is VehicleRepository.LoadingResult.Success -> {
                        _deleteState.value = DeleteVehicleState.Success
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> _deleteState.value = DeleteVehicleState.Error("Network Error")
                    else -> _deleteState.value =
                        DeleteVehicleState.Error(e.message ?: "Unknown Error")
                }
            }
        }

    }

    fun resetDeleteState() {
        _deleteState.value = DeleteVehicleState.Idle
    }


    sealed class JobCardsLoadState {
        data object Idle : JobCardsLoadState()
        data object Loading : JobCardsLoadState()
        data class Success(val jobCards: List<JobCard>) : JobCardsLoadState()
        data class Error(val message: String) : JobCardsLoadState()
    }

    sealed class VehicleLoadState {
        data object Idle : VehicleLoadState()
        data object Loading : VehicleLoadState()
        data object Success : VehicleLoadState()
        data class Error(val message: String) : VehicleLoadState()

    }

    sealed class DeleteVehicleState {
        data object Idle : DeleteVehicleState()
        data object Loading : DeleteVehicleState()
        data object Success : DeleteVehicleState()
        data class Error(val message: String) : DeleteVehicleState()
    }
}

class VehicleDetailsViewModelFactory(private val vehicleId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(VehicleDetailsViewModel::class.java)) {
            return VehicleDetailsViewModel(vehicleId = vehicleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}