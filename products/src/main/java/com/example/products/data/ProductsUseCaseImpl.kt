package com.example.products.data

import com.prodacc.data.remote.dao.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsUseCaseImpl @Inject constructor(
    private val repository: ProductsRepository
) : ProductsUseCase {
    override suspend fun getProducts(): Flow<Resource<List<Product>>> {
        return repository.getProducts()
    }
}