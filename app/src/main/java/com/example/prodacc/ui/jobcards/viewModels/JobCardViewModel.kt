package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import java.util.UUID

class JobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository()
): ViewModel(){
    val jobCards = jobCardRepository.generateJobCards(10)
    val pastJobCards = jobCardRepository.generateJobCards(5)

    val vehicles = vehicleRepository.getVehicles()

    private val _vehicleState : MutableState<Vehicle?> = mutableStateOf(null)
    val vehicleState = _vehicleState.value

    fun updateVehicle(vehicleId: UUID){
        val vehicle = vehicleRepository.getVehicleById(vehicleId)
        _vehicleState.value = vehicle
    }



}