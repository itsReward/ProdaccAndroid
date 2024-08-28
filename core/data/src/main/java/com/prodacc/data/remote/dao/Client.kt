package com.prodacc.data.remote.dao

import java.util.UUID

data class Client (
    val id : UUID,
    val clientName: String,
    val clientSurname: String,
    val gender: String,
    val jobTitle: String,
    val company: String,
    val phone: String,
    val email: String,
    val address: String,
    val vehicle: List<ClientVehicle>
)

data class ClientVehicle(
    val id: UUID,
    val model: String,
    val make: String
)