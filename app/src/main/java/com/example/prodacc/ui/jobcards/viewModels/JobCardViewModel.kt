package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class JobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository()
): ViewModel(){
    private val _jobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val jobCards: StateFlow<List<JobCard>> = _jobCards.asStateFlow()

    private val _pastJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val pastJobCards: StateFlow<List<JobCard>> = _pastJobCards.asStateFlow()

    init {
        viewModelScope.launch {
           updateJobCards()
        }
    }

    suspend fun updateJobCards(){

            _jobCards.value = jobCardRepository.getJobCards()

            println(_jobCards.value.size)

            _pastJobCards.value = jobCards.value.filter { it.dateAndTimeClosed != null }

    }

    fun updateJobCardsScreen(){
        println("ysahhh biyy")
        viewModelScope.launch {
            updateJobCards()
            println("phakathi inside")
        }
    }

    val vehicles = vehicleRepository.getVehicles()

    private val _vehicleState : MutableState<Vehicle?> = mutableStateOf(null)
    val vehicleState = _vehicleState.value

    fun updateVehicle(vehicleId: UUID){
        val vehicle = vehicleRepository.getVehicleById(vehicleId)
        _vehicleState.value = vehicle
    }
}