package com.prodacc.data.remote.dao

import java.util.UUID

data class ProductVehicle(
    val id: UUID,
    val make: String,
    val model: String,
    val year: String
)

data class NewProductVehicle(
    val make: String,
    val model: String,
    val year: String
)