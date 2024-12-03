package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.VehicleRepository
import java.io.IOException
import java.util.UUID

class EditVehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleId: UUID
) {
    private val _uiState = mutableStateOf<Vehicle?>(null)
    val uiState : State<Vehicle?> = _uiState

    val clients = emptyList<Client>()
    val vehicleModels = mapOf(
        "Jeep" to vehicleRepository.jeepModels,
        "Mercedes-Benz" to vehicleRepository.mercedesBenzModels
    )
    val make = listOf("Mercedes-Benz", "Jeep")


    private fun updateUiState(update: Vehicle.() -> Vehicle) {
        _uiState.value = _uiState.value?.update()
    }

    val vehicleMakeDropdown = mutableStateOf(false)
    val vehicleModelDropdown = mutableStateOf(false)
    val vehicleClientDropdown = mutableStateOf(false)


    suspend fun getVehicleEntity(id: String){
        try {
            val vehicle = vehicleRepository.getVehicleById(UUID.fromString(id))
            when (vehicle) {
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    _uiState.value = vehicle.vehicle
                }
                is VehicleRepository.LoadingResult.Error -> TODO()
                is VehicleRepository.LoadingResult.ErrorSingleMessage -> TODO()
                VehicleRepository.LoadingResult.NetworkError -> TODO()
                is VehicleRepository.LoadingResult.Success -> TODO()
                null -> TODO()
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> TODO()
                else -> TODO()
            }
        }
    }


    fun onVehicleMakeToggle() {
        vehicleMakeDropdown.value = !vehicleMakeDropdown.value
    }
    fun onVehicleModelToggle() {
        vehicleModelDropdown.value = !vehicleModelDropdown.value
    }
    fun onVehicleClientToggle() {
        vehicleClientDropdown.value = !vehicleClientDropdown.value
    }

    fun updateModel(model : String){
        updateUiState { copy(model = model) }
    }

    fun updateRegNumber(regNumber: String){
        updateUiState { copy(regNumber = regNumber) }
    }

    fun updateMake(make: String){
        updateUiState { copy(make = make) }
    }

    fun updateColor(color : String){
        updateUiState { copy(color = color) }
    }

    fun updateChassisNumber(chassisNumber : String){
        updateUiState { copy(chassisNumber = chassisNumber) }
    }

    fun updateClientId(client : Client) {
        updateUiState { copy(id = client.id) }
        updateUiState { copy(clientName = client.clientName, clientSurname = client.clientSurname) }
    }
}