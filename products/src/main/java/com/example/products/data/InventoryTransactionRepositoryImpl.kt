package com.example.products.data

import com.prodacc.data.remote.dao.product.InventoryTransaction
import com.prodacc.data.remote.dao.product.NewInventoryTransaction
import com.prodacc.data.remote.services.InventoryTransactionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryTransactionRepositoryImpl @Inject constructor(
    private val inventoryTransactionService: InventoryTransactionService
) : InventoryTransactionsRepository {
    override suspend fun getInventoryTransactions(): Flow<Resource<List<InventoryTransaction>>> {
        return flow {
            try {
                val response = inventoryTransactionService.getRecentInventoryTransactions()
                if (response.isSuccessful) {
                    response.body()?.let { transactions ->
                        emit(Resource.Success(transactions))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun getInventoryTransactionForProduct(id: UUID): Flow<Resource<List<InventoryTransaction>>> {
        return flow {
            try {
                val response = inventoryTransactionService.getInventoryTransactionsForProduct(id)
                if (response.isSuccessful) {
                    response.body()?.let { transaction ->
                        emit(Resource.Success(transaction))
                    } ?: emit(Resource.Error("Empty response body"))
                } else {
                    emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    override suspend fun addNewInventoryTransaction(transaction: NewInventoryTransaction): Flow<Resource<InventoryTransaction>> {
        return flow {
            try {
                val response = inventoryTransactionService.createInventoryTransaction(transaction)
                if (response.isSuccessful) {
                    response.body()?.let { newTransaction ->
                        emit(Resource.Success(newTransaction))
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