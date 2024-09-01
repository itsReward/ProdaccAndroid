package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.mutableStateOf
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository

class VehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val clientsRepository: ClientRepository = ClientRepository(),
    private val vehicleId: Any
) {
    val vehicle = vehicleRepository.getVehicleById(vehicleId)
    val vehicleJobCards = jobCardRepository.generateJobCards(20)
    val clientList = clientsRepository.getClientsList()


    val editVehicle = mutableStateOf(false)


    val editVehicleViewModel = EditVehicleDetailsViewModel(vehicle = vehicle)

    fun toggleEditVehicle () {
        editVehicle.value = !editVehicle.value
    }

    fun saveVehicle () {
        editVehicle.value = !editVehicle.value
    }
}