package com.prodacc.data.remote.dao.product

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.UUID

data class CreateProductCategory(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)

data class ProductCategory(
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("productCount") val productCount: Int
): Serializable

data class ProductCategoryWithProduct(
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("product") val product: List<Product>
): Serializable {
    fun toCategory(): ProductCategory {
        return ProductCategory(
            id = this.id,
            name = this.name,
            description = this.description,
            productCount = this.product.size
        )
    }
}