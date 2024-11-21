package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.launch
import java.util.UUID

class VehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val clientsRepository: ClientRepository = ClientRepository(),
    private val vehicleId: String
):ViewModel() {
    val vehicle = vehicleRepository.getVehicleById(UUID.fromString(vehicleId))
    var vehicleJobCards = emptyList<JobCard>()
    val clientList = clientsRepository.getClientsList()

    init {
        viewModelScope.launch {
            vehicleJobCards = jobCardRepository.getJobCards()
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
}