package com.example.products.data

import com.prodacc.data.remote.dao.product.InventoryTransaction
import com.prodacc.data.remote.dao.product.NewInventoryTransaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

interface InventoryTransactionsUseCase {
    suspend fun getInventoryTransactions(): Flow<Resource<List<InventoryTransaction>>>
    suspend fun getInventoryTransactionsByProductId(id: UUID): Flow<Resource<List<InventoryTransaction>>>
    suspend fun addNewInventoryTransaction(transaction: NewInventoryTransaction): Flow<Resource<InventoryTransaction>>

}