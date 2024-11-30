package com.example.prodacc.ui.vehicles.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prodacc.ui.vehicles.stateClasses.NewVehicleStateClass
import com.google.android.libraries.mapsplatform.transportation.consumer.model.Vehicle
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import com.prodacc.data.repositories.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NewVehicleViewModel(
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val clientRepository: ClientRepository = ClientRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow(NewVehicleStateClass())
    val uiState  = _uiState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    private val _clients = MutableStateFlow<LoadEntities>(LoadEntities(null, null))
    val clients = _clients.asStateFlow()

    init {
        viewModelScope.launch {
            fetchClients()
        }
    }

    val vehicleModels = mapOf( "Mercedes-Benz" to vehicleRepository.mercedesBenzModels,"Jeep" to  vehicleRepository.jeepModels)
    val make = listOf("Mercedes-Benz", "Jeep")


    private fun updateUiState(update: NewVehicleStateClass.() -> NewVehicleStateClass) {
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

    fun updateClientId(client : Client) {
        updateUiState { copy(clientId = client.id) }
        updateUiState { copy(clientName = client.clientName, clientSurname = client.clientSurname) }
    }

    fun resetSaveState(){
        _saveState.value = SaveState.Idle
    }

    fun saveVehicle(){
        if (_uiState.value.model == null || _uiState.value.regNumber == null || _uiState.value.make == null || _uiState.value.color == null || _uiState.value.chassisNumber == null || _uiState.value.clientId == null) {
            _saveState.value = SaveState.Error("Fill all details")
        } else {

            viewModelScope.launch {
                try {
                    _saveState.value = SaveState.Loading
                    val vehicle = vehicleRepository.createVehicle(
                        _uiState.value.toNewVehicle()
                    )
                    when (vehicle) {
                        is VehicleRepository.LoadingResult.Error -> {
                            SaveState.Error(vehicle.message ?: "Error")
                        }
                        is VehicleRepository.LoadingResult.ErrorSingleMessage -> {
                            SaveState.Error(vehicle.message)
                        }
                        VehicleRepository.LoadingResult.NetworkError -> {
                            SaveState.Error("Network Error")
                        }
                        is VehicleRepository.LoadingResult.SingleEntity -> {
                            _saveState.value = SaveState.Success(vehicle.vehicle!!)
                        }
                        is VehicleRepository.LoadingResult.Success -> {
                            //will never happen
                        }
                    }
                } catch (e: Exception){
                    _saveState.value = SaveState.Error(e.message ?: "Error saving vehicle")

                }

            }
        }


    }

    private fun NewVehicleStateClass.toNewVehicle(): com.prodacc.data.remote.dao.NewVehicle{
        return com.prodacc.data.remote.dao.NewVehicle(
            model = this.model!!,
            regNumber = regNumber!!,
            make = make!!,
            color = color!!,
            chassisNumber = chassisNumber!!,
            clientId = clientId!!,
            clientName = clientName!!,
            clientSurname = clientSurname!!
        )
    }

    private suspend fun fetchClients(){
        try {
            when (val response = clientRepository.getClients()){
                is ClientRepository.LoadingResult.Error -> {
                    _clients.value = LoadEntities(null, response.message)
                }
                is ClientRepository.LoadingResult.ErrorSingleMessage -> {
                    _clients.value = LoadEntities(null, response.message)
                }
                is ClientRepository.LoadingResult.NetworkError -> {
                    _clients.value = LoadEntities(null, "Network Error")
                }
                is ClientRepository.LoadingResult.SingleEntity -> {
                    _clients.value = LoadEntities(null, "Recieved Single Entity instead of list")
                }
                is ClientRepository.LoadingResult.Success -> {
                    _clients.value = LoadEntities(response.clients, null)
                }
            }
        } catch (e: Exception){
            _clients.value = LoadEntities(null, e.message)
        }

    }

    data class LoadEntities(
        val clients : List<Client>?,
        val error : String?
    )

    sealed class SaveState{
        data class Success(val vehicle: com.prodacc.data.remote.dao.Vehicle) : SaveState()
        data class Error(val message: String) : SaveState()
        data object Idle: SaveState()
        data object Loading: SaveState()
    }

}