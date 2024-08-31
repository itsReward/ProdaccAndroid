package com.example.prodacc.ui.clients.viewModels

import androidx.compose.runtime.mutableStateOf
import com.prodacc.data.repositories.ClientRepository
import java.util.UUID

class ClientDetailsViewModel(
    private val clientRepository: ClientRepository = ClientRepository(),
    private val clientId: UUID
) {
    private val _client = mutableStateOf(clientRepository.getClient(clientId))
    val client = _client.value



}