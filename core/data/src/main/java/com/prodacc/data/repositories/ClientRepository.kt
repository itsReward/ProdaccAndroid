package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.NewClient
import java.io.IOException
import java.util.UUID

class ClientRepository {
    private val service = ApiInstance.clientService


    suspend fun getClientsById(id: UUID): LoadingResult {
        return try {
            val response = service.getClient(id)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    return LoadingResult.SingleEntity(null, "Client not found")
                } else {
                    return LoadingResult.SingleEntity(response.body(), null)
                }
            } else{
                return LoadingResult.Error(response.raw().message)
            }
        } catch (e : Exception){
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getClients(): LoadingResult {
        return try {
            val response = service.getClients()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun createClient(newClient: NewClient): LoadingResult {
        return try {
            val response = service.createClient(newClient)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.ErrorSingleMessage("Client creation failed")
                } else {
                    LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Client creation error")
            }
        }
    }

    suspend fun updateClient(id: UUID, updatedClient: Client): LoadingResult {
        return try {
            val response = service.updateClient(id, updatedClient)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.ErrorSingleMessage("Client update failed")
                } else {
                    LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Client update error")
            }
        }
    }

    suspend fun deleteClient(id: UUID): LoadingResult {
        return try {
            val response = service.deleteClient(id)
            if (response.isSuccessful) {
                LoadingResult.Success(emptyList(), )
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Client deletion error")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val clients: List<Client>, val message: String? = null) : LoadingResult()
        data class Error(val message: String?) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val client: Client?, val error: String?) : LoadingResult()
    }
}