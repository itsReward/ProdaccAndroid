package com.example.products.data

import com.prodacc.data.remote.dao.product.CreateProductCategory
import com.prodacc.data.remote.dao.product.CreateProductVehicle
import com.prodacc.data.remote.dao.product.NewProduct
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductCategoryWithProduct
import com.prodacc.data.remote.dao.product.ProductVehicle
import com.prodacc.data.remote.dao.product.ProductVehicleWithProducts
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductsRepository {
    suspend fun getProducts(): Flow<Resource<List<Product>>>
    suspend fun getCategories(): Flow<Resource<List<ProductCategory>>>
    suspend fun getVehicleList(): Flow<Resource<List<ProductVehicle>>>

    suspend fun getProduct(id: UUID):Flow<Resource<Product>>
    suspend fun getCategoriesWithProduct() : Flow<Resource<List<ProductCategoryWithProduct>>>
    suspend fun getVehiclesWithProducts(): Flow<Resource<List<ProductVehicleWithProducts>>>

    suspend fun addNewProduct(product: NewProduct): Flow<Resource<Product>>
    suspend fun addNewVehicle(vehicle: CreateProductVehicle): Flow<Resource<ProductVehicle>>
    suspend fun addNewCategory(categories: CreateProductCategory): Flow<Resource<ProductCategory>>


    suspend fun deleteProduct(id: UUID): Flow<Resource<Unit>>
    suspend fun deleteVehicle(id: UUID): Flow<Resource<Unit>>
    suspend fun deleteCategory(id: UUID): Flow<Resource<Unit>>


    suspend fun updateProduct(id: UUID, product: Product): Flow<Resource<Product>>
    suspend fun updateVehicle(id: UUID, vehicle: ProductVehicle): Flow<Resource<ProductVehicle>>
    suspend fun updateCategory(id: UUID, category: ProductCategory): Flow<Resource<ProductCategory>>

}

