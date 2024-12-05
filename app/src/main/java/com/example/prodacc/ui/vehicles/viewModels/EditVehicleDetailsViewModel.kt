package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.prodacc.ui.clients.viewModels.ClientsViewModel.LoadState
import com.example.prodacc.ui.clients.viewModels.EditClientDetailsViewModel
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModel
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.NewVehicle
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class EditVehicleDetailsViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val clientRepository: ClientRepository = ClientRepository(),
    private val vehicleId: String
): ViewModel() {
    // State for the vehicle details
    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    //state for clients
    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients = _clients.asStateFlow()

    // Load state management
    private val _loadState = MutableStateFlow<LoadState>(LoadState.Idle)
    val loadState = _loadState.asStateFlow()

    private val _clientLoadState = MutableStateFlow<ClientLoadState>(ClientLoadState.Idle)
    val clientLoadState = _clientLoadState.asStateFlow()

    // Update state management
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState = _updateState.asStateFlow()

    private val _updateConfirmation = MutableStateFlow(false)
    val updateConfirmation = _updateConfirmation.asStateFlow()

    // Dropdown states
    val vehicleMakeDropdown = MutableStateFlow(false)
    val vehicleModelDropdown = MutableStateFlow(false)
    val vehicleClientDropdown = MutableStateFlow(false)

    // Constant lists and maps
    val vehicleModels = mapOf(
        "Jeep" to vehicleRepository.jeepModels,
        "Mercedes-Benz" to vehicleRepository.mercedesBenzModels
    )
    val make = listOf("Mercedes-Benz", "Jeep")


    // Initialize by fetching vehicle details
    init {
        viewModelScope.launch {
            fetchVehicle()
            fetchClients()
        }
    }

    fun toggleUpdateConfirmation(){
        _updateConfirmation.value = true
    }

    fun resetUpdateConfirmation(){
        _updateConfirmation.value = false
    }

    // Helper function to update vehicle state
    private fun updateVehicle(update: Vehicle.() -> Vehicle) {
        _vehicle.value = _vehicle.value?.update()
    }

    // Specific update methods
    fun updateModel(model: String) {
        updateVehicle { copy(model = model) }
    }

    fun updateRegNumber(regNumber: String) {
        updateVehicle { copy(regNumber = regNumber) }
    }

    fun updateMake(make: String) {
        updateVehicle { copy(make = make) }
    }

    fun updateColor(color: String) {
        updateVehicle { copy(color = color) }
    }

    fun updateChassisNumber(chassisNumber: String) {
        updateVehicle { copy(chassisNumber = chassisNumber) }
    }

    fun updateClientId(client: Client) {
        updateVehicle {
            copy(
                id = client.id,
                clientName = client.clientName,
                clientSurname = client.clientSurname
            )
        }
    }

    // Dropdown toggle methods
    fun onVehicleMakeToggle() {
        vehicleMakeDropdown.value = !vehicleMakeDropdown.value
    }

    fun onVehicleModelToggle() {
        vehicleModelDropdown.value = !vehicleModelDropdown.value
    }

    fun onVehicleClientToggle() {
        vehicleClientDropdown.value = !vehicleClientDropdown.value
    }

    // Fetch vehicle details
    private suspend fun fetchVehicle() {
        try {
            _loadState.value = LoadState.Loading
            val result = vehicleRepository.getVehicleById(UUID.fromString(vehicleId))

            when (result) {
                is VehicleRepository.LoadingResult.SingleEntity -> {
                    _vehicle.value = result.vehicle
                    _loadState.value = LoadState.Success
                }
                is VehicleRepository.LoadingResult.Error -> {
                    _loadState.value = LoadState.Error(result.message?: "Some Error Occurred")
                }
                is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                    _loadState.value = LoadState.Error(result.message)
                }
                VehicleRepository.LoadingResult.NetworkError -> {
                    _loadState.value = LoadState.Error("Network Error")
                }
                is VehicleRepository.LoadingResult.Success -> {
                    _loadState.value = LoadState.Error("Unexpected result type")
                }
                null -> {
                    _loadState.value = LoadState.Error("Vehicle not found")
                }
            }
        } catch (e: Exception) {
            _loadState.value = when (e) {
                is IOException -> LoadState.Error("Network Error")
                else -> LoadState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun fetchClients() {
        _clientLoadState.value = ClientLoadState.Loading
        try {
            when (val clients = clientRepository.getClients()) {
                is ClientRepository.LoadingResult.Success -> {
                    _clientLoadState.value = ClientLoadState.Success
                    _clients.value = clients.clients
                }

                is ClientRepository.LoadingResult.Error -> {
                    _clientLoadState.value = ClientLoadState.Error(clients.message ?: "Unknown Error")
                }

                is ClientRepository.LoadingResult.ErrorSingleMessage -> {
                    _clientLoadState.value = ClientLoadState.Error(clients.message)
                }

                ClientRepository.LoadingResult.NetworkError -> {
                    _clientLoadState.value = ClientLoadState.Error("Network Error")
                }

                is ClientRepository.LoadingResult.SingleEntity -> {
                    //will never happen
                    _clientLoadState.value = ClientLoadState.Error("returned a single entity instead of a list")
                }
            }
        } catch (e: Exception) {
            _clientLoadState.value = ClientLoadState.Error(e.message ?: "Unknown Error")
        }

    }

    // Update vehicle details
    fun updateVehicleDetails() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            _updateConfirmation.value = false
            try {
                val currentVehicle = _vehicle.value
                if (currentVehicle != null) {
                    val newVehicle = NewVehicle(
                        make = currentVehicle.make,
                        model = currentVehicle.model,
                        regNumber = currentVehicle.regNumber,
                        color = currentVehicle.color,
                        chassisNumber = currentVehicle.chassisNumber,
                        clientId = currentVehicle.clientId,
                        clientName = currentVehicle.clientName,
                        clientSurname = currentVehicle.clientSurname
                    )

                    val result = vehicleRepository.updateVehicle(UUID.fromString(vehicleId), newVehicle)

                    when (result) {
                        is VehicleRepository.LoadingResult.SingleEntity -> {
                            _updateState.value = UpdateState.Success
                            _vehicle.value = result.vehicle
                        }
                        is VehicleRepository.LoadingResult.Error -> {
                            _updateState.value = UpdateState.Error(result.message?: "Some Error occurred")
                        }
                        VehicleRepository.LoadingResult.NetworkError -> {
                            _updateState.value = UpdateState.Error("Network Error")
                        }
                        else -> {
                            _updateState.value = UpdateState.Error("Unknown Error")
                        }
                    }
                } else {
                    _updateState.value = UpdateState.Error("No vehicle data")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun refreshVehicle() {
        viewModelScope.launch {
            fetchVehicle()
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    // State classes for load and update states
    sealed class LoadState {
        data object Idle : LoadState()
        data object Loading : LoadState()
        data object Success : LoadState()
        data class Error(val message: String) : LoadState()
    }

    sealed class UpdateState {
        data object Idle : UpdateState()
        data object Loading : UpdateState()
        data object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    sealed class ClientLoadState{
        data object Idle : ClientLoadState()
        data object Loading : ClientLoadState()
        data object Success : ClientLoadState()
        data class Error (val message: String) : ClientLoadState()
    }
}

class EditVehicleDetailsViewModelFactory(private val vehicleId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(EditVehicleDetailsViewModel::class.java)) {
            return EditVehicleDetailsViewModel(vehicleId = vehicleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}