package com.prodacc.data.remote.dao.product

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class Product(
    @SerializedName("productId") val productId: UUID,
    @SerializedName("productCode") val productCode: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("unitOfMeasure") val unitOfMeasure: String?,
    @SerializedName("currentStock") val currentStock: Int,
    @SerializedName("minimumStock") val minimumStock: Int,
    @SerializedName("maximumStock") val maximumStock: Int,
    @SerializedName("costPrice") val costPrice: Float,
    @SerializedName("sellingPrice") val sellingPrice: Float,
    @SerializedName("markupPercentage") val markupPercentage: Float,
    @SerializedName("supplierName") val supplierName: String?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("createdAt") val createdAt: LocalDateTime,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("isLowStock") val isLowStock: Boolean
)

data class NewProduct(
    @SerializedName("productCode") val productCode: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("categoryId") val categoryId: UUID? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("unitOfMeasure") val unitOfMeasure: String? = null,
    @SerializedName("minimumStock") val minimumStock: Int,
    @SerializedName("maximumStock") val maximumStock: Int,
    @SerializedName("costPrice") val costPrice: Float,
    @SerializedName("sellingPrice") val sellingPrice: Float,
    @SerializedName("markupPercentage") val markupPercentage: Float,
    @SerializedName("supplierId") val supplierId: UUID? = null
)