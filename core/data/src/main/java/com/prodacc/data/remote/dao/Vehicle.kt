package com.prodacc.data.remote.dao

import java.util.UUID

data class Vehicle (
    val id : UUID,
    val model : String,
    val regNumber : String,
    val make : String,
    val color : String,
    val chassisNumber: String,
    val clientId: UUID,
    val clientName : String,
    val clientSurname : String
)
