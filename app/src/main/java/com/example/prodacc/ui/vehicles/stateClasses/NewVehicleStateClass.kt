package com.example.prodacc.ui.vehicles.stateClasses

import java.util.UUID

data class NewVehicleStateClass (
    val model: String? = null,
    val regNumber: String? = null,
    val make: String? = null,
    val color: String? = null,
    val chassisNumber: String? = null,
    val clientId: UUID? = null,
    val clientName: String? = null,
    val clientSurname: String? = null
)