package com.example.prodacc.ui.clients

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.repositories.ClientRepository

class ClientsViewModel(
    private val clientRepository: ClientRepository = ClientRepository()
) : ViewModel() {
    private val _clients = mutableStateOf(clientRepository.getClients())
    val clients = _clients.value.groupBy { it.clientName.first() }.toSortedMap()

}


