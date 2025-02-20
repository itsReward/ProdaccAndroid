package com.prodacc.data.remote.dao

import java.util.UUID

data class Product(
    val id: UUID,
    val partNumber: String,
    val partName: String,
    val category: String,
    val description: String,
    val inStock: Float,
    val healthyNumber: Float,
    val price: Float,
    val storageLocation: String
)

data class NewProduct(
    val partNumber: String,
    val partName: String,
    val category: String,
    val description: String? = null,
    val inStock: Float,
    val healthyNumber: Float,
    val price: Float? = null ,
    val storageLocation: String
)
