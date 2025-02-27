package com.prodacc.data.remote.dao.product

import java.util.UUID

data class CreateProductVehicle(
    val make: String,
    val model: String,
    val year: Int
)

data class ProductVehicle(
    val id: UUID,
    val make: String,
    val model: String,
    val year: Int,
    val productCount: Int
)

data class ProductVehicleWithProducts(
    val id: UUID,
    val make: String,
    val model: String,
    val year: Int,
    val products: List<Product>
)

