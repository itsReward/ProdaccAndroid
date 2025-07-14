package com.example.products.data

import com.prodacc.data.remote.dao.product.CreateProductCategory
import com.prodacc.data.remote.dao.product.CreateProductVehicle
import com.prodacc.data.remote.dao.product.NewProduct
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductCategoryWithProduct
import com.prodacc.data.remote.dao.product.ProductVehicle
import com.prodacc.data.remote.dao.product.ProductVehicleWithProducts
import com.prodacc.data.remote.services.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepositoryImpl @Inject constructor(
    private val productService: ProductService
) : ProductsRepository
{
    override suspend fun getProducts(): Flow<Resource<List<Product>>> {
        return flow {
            try {
                val response = productService.getProducts()
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        emit(Resource.Success(products))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<ProductCategory>>> {
        return flow {
            try {
                val response = productService.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        emit(Resource.Success(categories))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getVehicleList(): Flow<Resource<List<ProductVehicle>>> {
        return flow {
            try {
                val response = productService.getVehicleList()
                if (response.isSuccessful) {
                    response.body()?.let { productVehicles ->
                        emit(Resource.Success(productVehicles))
                    } ?: emit(Resource.Error("Empty response Body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getVehiclesWithProducts(): Flow<Resource<List<ProductVehicleWithProducts>>> {
        return flow {
            try {
                val response = productService.getAllVehiclesWithProducts()
                if (response.isSuccessful) {
                    response.body()?.let { productVehicleWithProducts ->
                        emit(Resource.Success(productVehicleWithProducts))
                    } ?: emit(Resource.Error("Empty response Body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getProduct(id: UUID): Flow<Resource<Product>> {
        return flow {
            try {
                val response = productService.getProductById(id)
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        emit(Resource.Success(product))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getCategoriesWithProduct(): Flow<Resource<List<ProductCategoryWithProduct>>> {
        return flow {
            try {
                val response = productService.getAllCategoriesWithProducts()
                if (response.isSuccessful) {
                    response.body()?.let { category ->
                        emit(Resource.Success(category))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun addNewProduct(product: NewProduct): Flow<Resource<Product>> {
        return flow {
            try {
                val response = productService.addNewProduct(product)
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        emit(Resource.Success(product))
                    } ?: emit(Resource.Error("Empty Response Body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun addNewVehicle(vehicle: CreateProductVehicle): Flow<Resource<ProductVehicle>> {
        return flow {
            try {
                val response = productService.addNewVehicle(vehicle)
                if (response.isSuccessful){
                    response.body()?.let { vehicle ->
                        emit(Resource.Success(vehicle))
                    }?:emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun addNewCategory(categories: CreateProductCategory): Flow<Resource<ProductCategory>> {
        return flow {
            try {
                val response = productService.addNewCategory(categories)
                if (response.isSuccessful){
                    response.body()?.let { category ->
                        emit(Resource.Success(category))
                    }?:emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
                }
            }
    }

    override suspend fun addVehicleToProduct(
        productId: UUID,
        vehicleId: UUID
    ): Flow<Resource<Unit>> {
        return flow {
            try {
                val response = productService.addVehicleToProduct(productId, vehicleId)
                if (response.isSuccessful){
                    emit(Resource.Success(Unit))
                }
            } catch (e: Exception){
                emit(Resource.Error(e.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun deleteProduct(id: UUID): Flow<Resource<Unit>> {
        return flow {
            try {
                val response = productService.deleteProduct(id)
                if (response.isSuccessful) {
                    emit(Resource.Success(Unit))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun deleteVehicle(id: UUID): Flow<Resource<Unit>> {
        return flow {
            try {
                val response = productService.deleteVehicle(id)
                if (response.isSuccessful){
                    emit(Resource.Success(Unit))
                }
            } catch (e: Exception){
                emit(Resource.Error(e.message?:"Unknown Error"))
            }
        }
    }

    override suspend fun deleteCategory(id: UUID): Flow<Resource<Unit>> {
        return flow {
            try {
                val response = productService.deleteCategory(id)
                if (response.isSuccessful) {
                    emit(Resource.Success(Unit))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun updateProduct(id: UUID, product: Product): Flow<Resource<Product>> {
        val updatedProduct = NewProduct(
            partName = product.partName,
            partNumber = product.partNumber,
            description = product.description,
            arrivalPrice = product.arrivalPrice,
            sellingPrice = product.sellingPrice.toFloat(),
            healthyNumber = product.healthyNumber,
            storageLocation = product.storageLocation,
            inStock = product.inStock
        )
        return flow {
            try {
                val response = productService.updateProduct(id, updatedProduct)
                if (response.isSuccessful) {
                    response.body()?.let { updatedProduct ->
                        emit(Resource.Success(updatedProduct))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun updateVehicle(
        id: UUID,
        vehicle: ProductVehicle
    ): Flow<Resource<ProductVehicle>> {
        return flow {
            try {
                val response = productService.updateVehicle(id, vehicle)
                if (response.isSuccessful) {
                    response.body()?.let { emit(Resource.Success(it)) }
                        ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun updateCategory(
        id: UUID,
        category: ProductCategory
    ): Flow<Resource<ProductCategory>> {
        return flow {
            try {
                val response = productService.updateCategory(id, category)
                if (response.isSuccessful) {
                    response.body()?.let { updatedCategory ->
                        emit(Resource.Success(updatedCategory))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }
}