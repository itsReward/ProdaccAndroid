package com.prodacc.data.remote.dao.product

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CreateProductVehicle(
    val make: String? = null,
    val model: String? = null,
    val year: Int? = null
)

data class ProductVehicle(
    @SerializedName("id") val id: UUID,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("productCount") val productCount: Int
)

data class ProductVehicleWithProducts(
    @SerializedName("id") val id: UUID,
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("products") val products: List<Product>
)

