package com.example.products.data

import com.prodacc.data.remote.dao.Product
import kotlinx.coroutines.flow.Flow

// Use Case
interface ProductsUseCase {
    suspend fun getProducts(): Flow<Resource<List<Product>>>
}

