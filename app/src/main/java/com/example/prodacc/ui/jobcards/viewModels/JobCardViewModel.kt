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

    private val _jobCardLoadState = MutableStateFlow<JobCardLoadState>(JobCardLoadState.Idle)
    val jobCardLoadState = _jobCardLoadState

    private val _pastJobCards = MutableStateFlow<List<JobCard>>(emptyList())
    val pastJobCards: StateFlow<List<JobCard>> = _pastJobCards.asStateFlow()

    init {
        viewModelScope.launch {
           updateJobCards()
        }
    }

    suspend fun updateJobCards(){
        _jobCardLoadState.value = JobCardLoadState.Loading

            jobCardRepository.getJobCards().let { loadingResult ->
                when (loadingResult) {
                    is JobCardRepository.LoadingResult.Success -> {
                        _jobCardLoadState.value = JobCardLoadState.Success(loadingResult.jobCards)
                        _jobCards.value = loadingResult.jobCards
                    }

                    is JobCardRepository.LoadingResult.Error -> {
                        _jobCardLoadState.value = JobCardLoadState.Error("error loading job cards")
                        //println(loadingResult.message)
                    }

                    else -> {
                        _jobCardLoadState.value = JobCardLoadState.Idle
                    }

                }
            }

            println(_jobCards.value.size)
            println("#########")
            _jobCards.value.forEach {
                println(it)
            }

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

sealed class JobCardLoadState {
    data object Idle : JobCardLoadState()
    data object Loading : JobCardLoadState()
    data class Success(val jobCards: List<JobCard>) : JobCardLoadState()
    data class Error(val message: String) : JobCardLoadState()

}