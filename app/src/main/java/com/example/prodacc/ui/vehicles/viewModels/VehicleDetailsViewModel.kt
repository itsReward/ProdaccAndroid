package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val clientsRepository: ClientRepository = ClientRepository(),
    private val vehicleId: String
):ViewModel() {
    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    private val _vehicleJobCards = MutableStateFlow<JobCardsLoadState>(JobCardsLoadState.Idle)
    val vehicleJobCards = _vehicleJobCards.asStateFlow()

    private val _loadState = MutableStateFlow<VehicleLoadState>(VehicleLoadState.Idle)
    val loadState = _loadState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchVehicle()
            //fetchJobCards()
        }
    }

    val editVehicle = mutableStateOf(false)


    //val editVehicleViewModel = EditVehicleDetailsViewModel(vehicle = vehicle)

    fun toggleEditVehicle () {
        editVehicle.value = !editVehicle.value
    }

    fun saveVehicle () {
        editVehicle.value = !editVehicle.value
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

    private suspend fun fetchVehicle(){
        _loadState.value = VehicleLoadState.Loading
        val id :Any = try {
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

    suspend fun fetchJobCards(){
        try {
            _vehicleJobCards.value = JobCardsLoadState.Loading
            val response = jobCardRepository.getJobCards()
            when (response){
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
                    _vehicleJobCards.value = JobCardsLoadState.Success(response.jobCards.filter { it.vehicleId == UUID.fromString(vehicleId)  })
                }
            }

        } catch (e: Exception){
            _vehicleJobCards.value = JobCardsLoadState.Error(e.message?: "Unknown Error")
        }
    }


    sealed class JobCardsLoadState{
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
}