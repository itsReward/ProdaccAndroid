package com.prodacc.data.remote.dao.product

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class InventoryTransaction(
    @SerializedName("transactionId") val transactionId: UUID,
    @SerializedName("productCode") val productCode: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitCost") val unitCost: BigDecimal?,
    @SerializedName("totalAmount") val totalAmount: BigDecimal?,
    @SerializedName("referenceType") val referenceType: String?,
    @SerializedName("referenceId") val referenceId: UUID?,
    @SerializedName("transactionDate") val transactionDate: LocalDateTime,
    @SerializedName("notes") val notes: String?,
    @SerializedName("createdBy") val createdBy: UUID?
)

data class NewInventoryTransaction(
    @SerializedName("productId") val productId: UUID,
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitCost") val unitCost: BigDecimal?,
    @SerializedName("referenceType") val referenceType: String?,
    @SerializedName("referenceId") val referenceId: UUID?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("createdBy") val createdBy: UUID?
)
