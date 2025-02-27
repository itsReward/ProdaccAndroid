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
import javax.inject.Inject

class ProductsUseCaseImpl @Inject constructor(
    private val repository: ProductsRepository
) : ProductsUseCase {
    override suspend fun getProducts(): Flow<Resource<List<Product>>> {
        return repository.getProducts()
    }

    override suspend fun getCategories(): Flow<Resource<List<ProductCategory>>> {
        return repository.getCategories()
    }

    override suspend fun getVehicleList(): Flow<Resource<List<ProductVehicle>>> {
        return repository.getVehicleList()
    }

    override suspend fun getProductById(id: UUID): Flow<Resource<Product>> {
        return repository.getProduct(id)
    }

    override suspend fun getCategoriesWithProducts(): Flow<Resource<List<ProductCategoryWithProduct>>> {
        return repository.getCategoriesWithProduct()
    }

    override suspend fun getVehiclesWithProducts(): Flow<Resource<List<ProductVehicleWithProducts>>> {
        return repository.getVehiclesWithProducts()
    }

    override suspend fun addNewProduct(product: NewProduct): Flow<Resource<Product>> {
        return repository.addNewProduct(product)
    }

    override suspend fun addNewVehicle(vehicle: CreateProductVehicle): Flow<Resource<ProductVehicle>> {
        return repository.addNewVehicle(vehicle)
    }

    override suspend fun addNewCategory(categories: CreateProductCategory): Flow<Resource<ProductCategory>> {
        return repository.addNewCategory(categories)
    }

    override suspend fun deleteProduct(id: UUID): Flow<Resource<Unit>> {
        return repository.deleteProduct(id)
    }

    override suspend fun deleteVehicle(id: UUID): Flow<Resource<Unit>> {
        return repository.deleteVehicle(id)
    }

    override suspend fun deleteCategory(id: UUID): Flow<Resource<Unit>> {
        return repository.deleteCategory(id)
    }

    override suspend fun updateProduct(id: UUID, product: Product): Flow<Resource<Product>> {
        return repository.updateProduct(id, product)
    }

    override suspend fun updateVehicle(id: UUID, vehicle: ProductVehicle): Flow<Resource<ProductVehicle>> {
        return repository.updateVehicle(id, vehicle)
    }

    override suspend fun updateCategory(id: UUID, category: ProductCategory): Flow<Resource<ProductCategory>> {
        return repository.updateCategory(id, category)
    }
}