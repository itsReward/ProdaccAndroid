package com.example.products.di

import com.example.products.data.ProductsRepository
import com.example.products.data.ProductsRepositoryImpl
import com.example.products.data.ProductsUseCase
import com.example.products.data.ProductsUseCaseImpl
import com.prodacc.data.remote.ApiServiceContainer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductsModule {
    @Provides
    @Singleton
    fun provideProductsRepository(
        apiServiceContainer: ApiServiceContainer
    ) : ProductsRepository {
        return ProductsRepositoryImpl(apiServiceContainer.productService)
    }

    @Provides
    @Singleton
    fun provideProductsUseCase(repository: ProductsRepository): ProductsUseCase {
        return ProductsUseCaseImpl(repository)
    }
}