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
        TODO("Not yet implemented")
    }

    override suspend fun addNewCategory(categories: CreateProductCategory): Flow<Resource<ProductCategory>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(id: UUID): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVehicle(id: UUID): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCategory(id: UUID): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(id: UUID, product: Product): Flow<Resource<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateVehicle(
        id: UUID,
        vehicle: ProductVehicle
    ): Flow<Resource<ProductVehicle>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(
        id: UUID,
        category: ProductCategory
    ): Flow<Resource<ProductCategory>> {
        TODO("Not yet implemented")
    }


}