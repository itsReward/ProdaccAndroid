package com.example.prodacc.ui.vehicles

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodacc.MyApplication
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VehiclesViewModel (
    private val vehicleRepository: VehicleRepository = VehicleRepository()
) : ViewModel()  {
    private val _vehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    private val _vehicleLoadState = MutableStateFlow<VehicleLoadState>(VehicleLoadState.Idle)
    val vehicleLoadState = _vehicleLoadState.asStateFlow()

    private val _activeVehicles = _vehicles.asStateFlow()
    val activeVehicles: StateFlow<List<Vehicle>> = _activeVehicles

    init {
        viewModelScope.launch {
            getVehicles()
        }
    }


    fun refreshVehicles() {
        viewModelScope.launch {
            getVehicles()
        }
    }

    private suspend fun getVehicles(){
        _vehicleLoadState.value = VehicleLoadState.Loading

        vehicleRepository.getVehicles().let { loadingResult ->
            when (loadingResult) {
                is VehicleRepository.LoadingResult.Success -> {
                    _vehicleLoadState.value = VehicleLoadState.Success
                    _vehicles.value = loadingResult.vehicles
                }
                is VehicleRepository.LoadingResult.Error -> {
                    _vehicleLoadState.value = VehicleLoadState.Error(loadingResult.message?: "unknown error")
                }

                is VehicleRepository.LoadingResult.ErrorSingleMessage ->{
                    _vehicleLoadState.value = VehicleLoadState.Error(loadingResult.message)
                }
                VehicleRepository.LoadingResult.NetworkError -> {
                    _vehicleLoadState.value = VehicleLoadState.Error("Network Error")
                }
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    //will never happen
                }
                else -> {}
            }
        }
    }
    val allVehicles = mutableStateOf(true)
    val workshopVehicles = mutableStateOf(false)

    fun onAllVehiclesClick() {
        allVehicles.value = true
        workshopVehicles.value = false
    }

    fun onWorkshopVehiclesClick() {
        allVehicles.value = false
        workshopVehicles.value = true
    }


    sealed class VehicleLoadState {
        data object Idle : VehicleLoadState()
        data object Loading : VehicleLoadState()
        data object Success : VehicleLoadState()
        data class Error(val message: String) : VehicleLoadState()

    }
}


