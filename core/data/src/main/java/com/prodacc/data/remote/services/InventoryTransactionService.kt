package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.product.InventoryTransaction
import com.prodacc.data.remote.dao.product.NewInventoryTransaction
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.UUID

interface InventoryTransactionService {
    @GET("/inventory-transactions/recent")
    suspend fun getRecentInventoryTransactions(): Response<List<InventoryTransaction>>
    @GET("/inventory-transactions/product/{productId}")
    suspend fun getInventoryTransactionsForProduct(productId: UUID): Response<List<InventoryTransaction>>

    @GET("/inventory-transactions/vehicle/{vehicleId}")
    suspend fun getInventoryTransactionsForVehicle(vehicleId: UUID): Response<List<InventoryTransaction>>

    @POST("/inventory-transactions/new")
    suspend fun createInventoryTransaction(transaction: NewInventoryTransaction): Response<InventoryTransaction>


}