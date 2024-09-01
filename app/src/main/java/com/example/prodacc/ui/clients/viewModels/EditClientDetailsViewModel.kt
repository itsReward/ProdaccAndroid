package com.example.prodacc.ui.clients.viewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.repositories.ClientRepository
import java.util.UUID

class EditClientDetailsViewModel(
    private val clientRepository: ClientRepository = ClientRepository(),
    private val clientId: UUID
):ViewModel() {
    private val _client = mutableStateOf(clientRepository.getClient(clientId))
    val client = _client.value

    val dropGenderToggle = mutableStateOf(false)

    private fun updateUiState( update : Client.() -> Client ){
        _client.value = _client.value.update()
    }

    fun toggleGender(){
        dropGenderToggle.value = !dropGenderToggle.value
    }

    fun updateFirstName(firstName: String){
        updateUiState { copy(clientName = firstName) }
    }
    fun updateSurname(surname: String){
        updateUiState { copy(clientSurname = surname) }
    }
    fun updateGender(gender: String){
        updateUiState { copy(gender = gender) }
        toggleGender()
    }
    fun updatePhone(phone: String){
        updateUiState { copy(phone = phone) }
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
}