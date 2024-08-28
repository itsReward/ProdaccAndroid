package com.example.prodacc.ui.vehicles

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.VehicleRepository
import java.util.UUID

class VehiclesViewModel (
    private val vehicleRepository: VehicleRepository = VehicleRepository()
) : ViewModel()  {
    val vehiclesList: List<Vehicle> = vehicleRepository.getVehicles()
    val activeVehicles = vehicleRepository.getVehicles(5)

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
}

