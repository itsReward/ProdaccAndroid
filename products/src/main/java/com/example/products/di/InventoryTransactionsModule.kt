package com.example.products.di

import com.example.products.data.InventoryTransactionRepositoryImpl
import com.example.products.data.InventoryTransactionsRepository
import com.example.products.data.InventoryTransactionsUseCase
import com.example.products.data.InventoryTransactionsUseCaseImpl
import com.example.products.data.ProductsRepository
import com.example.products.data.ProductsRepositoryImpl
import com.example.products.data.ProductsUseCase
import com.example.products.data.ProductsUseCaseImpl
import com.prodacc.data.remote.ApiServiceContainer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InventoryTransactionsModule {
    @Provides
    @Singleton
    fun provideInventoryTransactionsRepository(
        apiServiceContainer: ApiServiceContainer
    ) : InventoryTransactionsRepository {
        return InventoryTransactionRepositoryImpl(apiServiceContainer.inventoryTransactionService)
    }

    @Provides
    @Singleton
    fun provideInventoryTransactionUseCase(repository: InventoryTransactionsRepository): InventoryTransactionsUseCase {
        return InventoryTransactionsUseCaseImpl(repository)
    }

}