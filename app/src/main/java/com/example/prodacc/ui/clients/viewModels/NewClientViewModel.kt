package com.example.prodacc.ui.clients.viewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.prodacc.ui.clients.stateClasses.AddClientState
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import java.util.UUID

class NewClientViewModel(
    private val clientRepository: ClientRepository = ClientRepository()
):ViewModel() {
    private val _state = mutableStateOf(AddClientState())
    val state = _state.value

    val dropGenderToggle = mutableStateOf(false)

    private fun updateUiState( update : AddClientState.() -> AddClientState ){
        _state.value = _state.value.update()
    }

    fun toggleGender(){
        dropGenderToggle.value = !dropGenderToggle.value
    }

    fun updateFirstName(firstName: String){
        updateUiState { copy(firstName = firstName) }
    }
    fun updateSurname(surname: String){
        updateUiState { copy(secondName = surname) }
    }
    fun updateGender(gender: String){
        updateUiState { copy(gender = gender) }
        toggleGender()
    }
    fun updatePhone(phone: String){
        updateUiState { copy(phoneNumber = phone) }
    }
    fun updateEmail(email: String){
        updateUiState { copy(email = email) }
    }
    fun updateAddress(address: String){
        updateUiState { copy(address = address) }
    }
    fun updateJobTitle(jobTitle: String){
        updateUiState { copy(jobTitle=jobTitle) }
    }
    fun updateCompany(company : String){
        updateUiState { copy(company = company) }
    }

    fun saveClient(){

    }
}