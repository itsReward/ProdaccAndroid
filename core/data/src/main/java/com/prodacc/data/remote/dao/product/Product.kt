package com.prodacc.data.remote.dao.product

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Product(
    @SerializedName("id") val id: UUID,
    @SerializedName("partNumber") val partNumber: String,
    @SerializedName("partName") val partName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("inStock") val inStock: Float,
    @SerializedName("healthyNumber") val healthyNumber: Float,
    @SerializedName("arrivalPrice") val arrivalPrice: Float,
    @SerializedName("sellingPrice") val sellingPrice: String,
    @SerializedName("storageLocation") val storageLocation: String,
    @SerializedName("version") val version: Long,
    @SerializedName("categories") val categories: List<ProductCategory>,
    @SerializedName("vehicles") val vehicles: List<ProductVehicle>
)

data class NewProduct(
    @SerializedName("partNumber") val partNumber: String? = null,
    @SerializedName("partName") val partName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("inStock") val inStock: Float? = null,
    @SerializedName("healthyNumber") val healthyNumber: Float? = null,
    @SerializedName("arrivalPrice") val arrivalPrice: Float? = null,
    @SerializedName("sellingPrice") val sellingPrice: Float? = null,
    @SerializedName("storageLocation") val storageLocation: String? = null
)
