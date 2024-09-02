package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.VehicleRepository
import java.util.UUID

class EditVehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleId: UUID
) {
    private val _uiState = mutableStateOf(vehicleRepository.getVehicleById(vehicleId))
    val uiState : State<Vehicle> = _uiState

    val clients = clientRepository.getClientsList()
    val vehicleModels = vehicleRepository.mercedesModels
    val make = listOf("Mercedes-Benz", "Jeep")


    private fun updateUiState(update: Vehicle.() -> Vehicle) {
        _uiState.value = _uiState.value.update()
    }

    val vehicleMakeDropdown = mutableStateOf(false)
    val vehicleModelDropdown = mutableStateOf(false)
    val vehicleClientDropdown = mutableStateOf(false)

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

    fun updateClientId(id : UUID) {
        updateUiState { copy(id = id) }
        val client = clientRepository.getClient(id)
        updateUiState { copy(clientName = client.clientName, clientSurname = client.clientSurname) }
    }
}