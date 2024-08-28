package com.example.prodacc.ui.vehicles.stateClasses

import java.util.UUID

data class UpdateVehicleState(
    val id: UUID,
    val model: String,
    val regNumber: String,
    val make: String,
    val color: String,
    val chassisNumber: String,
    val clientId: UUID,
    val clientName: String,
    val clientSurname: String,
)
