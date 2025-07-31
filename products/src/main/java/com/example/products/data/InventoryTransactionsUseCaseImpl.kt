package com.example.products.data

import com.prodacc.data.remote.dao.product.InventoryTransaction
import com.prodacc.data.remote.dao.product.NewInventoryTransaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class InventoryTransactionsUseCaseImpl @Inject constructor(
    private val inventoryTransactionsRepository: InventoryTransactionsRepository
) : InventoryTransactionsUseCase {
    override suspend fun getInventoryTransactions(): Flow<Resource<List<InventoryTransaction>>> {
        return inventoryTransactionsRepository.getInventoryTransactions()
    }
    override suspend fun getInventoryTransactionsByProductId(id: UUID): Flow<Resource<List<InventoryTransaction>>> {
        return inventoryTransactionsRepository.getInventoryTransactionForProduct(id)
    }
    override suspend fun addNewInventoryTransaction(transaction: NewInventoryTransaction): Flow<Resource<InventoryTransaction>> {
        return inventoryTransactionsRepository.addNewInventoryTransaction(transaction)
    }


}