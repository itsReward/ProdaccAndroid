package com.example.products.data

import com.prodacc.data.remote.dao.NewPartCategories
import com.prodacc.data.remote.dao.NewProduct
import com.prodacc.data.remote.dao.NewProductVehicle
import com.prodacc.data.remote.dao.PartCategories
import com.prodacc.data.remote.dao.Product
import com.prodacc.data.remote.dao.ProductVehicle
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductsRepository {
    suspend fun getProducts(): Flow<Resource<List<Product>>>
    suspend fun getCategories(): Flow<Resource<List<PartCategories>>>
    suspend fun getVehicleList(): Flow<Resource<List<ProductVehicle>>>
    suspend fun addNewProduct(product: NewProduct): Flow<Resource<Product>>
    suspend fun addNewVehicle(vehicle: NewProductVehicle): Flow<Resource<ProductVehicle>>
    suspend fun addNewCategory(categories: NewPartCategories): Flow<Resource<PartCategories>>
    suspend fun deleteProduct(id: UUID): Flow<Resource<Unit>>
    suspend fun deleteVehicle(id: UUID): Flow<Resource<Unit>>
    suspend fun deleteCategory(id: UUID): Flow<Resource<Unit>>
    suspend fun updateProduct(id: UUID, product: Product): Flow<Resource<Product>>
    suspend fun updateVehicle(id: UUID, vehicle: ProductVehicle): Flow<Resource<ProductVehicle>>
    suspend fun updateCategory(id: UUID, category: PartCategories): Flow<Resource<PartCategories>>

}

