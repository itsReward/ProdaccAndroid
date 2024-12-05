package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.NewVehicle
import com.prodacc.data.remote.dao.Vehicle
import com.prodacc.data.remote.services.VehicleService
import okhttp3.ResponseBody
import java.io.IOException
import java.util.UUID

class VehicleRepository {
    private val vehicleService : VehicleService = ApiInstance.vehicleService

    val mercedesBenzModels = listOf(
        // Sedans
        "A-Class Sedan",
        "C-Class Sedan",
        "E-Class Sedan",
        "S-Class Sedan",
        "CLA Coupe",
        "CLS Coupe",

        // SUVs
        "GLA SUV",
        "GLB SUV",
        "GLC SUV",
        "GLE SUV",
        "GLE Coupe",
        "GLS SUV",
        "G-Class SUV",

        // Coupes
        "C-Class Coupe",
        "E-Class Coupe",
        "S-Class Coupe",
        "CLA Coupe",
        "AMG GT Coupe",

        // Cabriolets/Roadsters
        "C-Class Cabriolet",
        "E-Class Cabriolet",
        "S-Class Cabriolet",
        "SL Roadster",
        "SLC Roadster",
        "AMG GT Roadster",

        // Electric Models
        "EQB SUV",
        "EQA SUV",
        "EQC SUV",
        "EQS Sedan",
        "EQS SUV",
        "EQE Sedan",
        "EQE SUV",

        // Vans
        "Sprinter",
        "Metris",

        // AMG Performance Models
        "AMG A-Class",
        "AMG CLA Coupe",
        "AMG GLA SUV",
        "AMG GLB SUV",
        "AMG GLC SUV",
        "AMG GLE SUV",
        "AMG GLS SUV",
        "AMG GT 4-Door Coupe",
        "AMG GT Coupe",
        "AMG GT Roadster",
        "AMG C-Class",
        "AMG E-Class",
        "AMG S-Class",

        // Others
        "Maybach S-Class",
        "Maybach GLS",
        "AMG Project ONE"
    )
    val jeepModels = listOf(
        "Wrangler",
        "Grand Cherokee",
        "Cherokee",
        "Liberty",
        "Compass",
        "Patriot",
        "Commander"
    )


    sealed class LoadingResult {
        data class Success(val vehicles: List<Vehicle>) : LoadingResult()
        data class Error(val message: String?) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val vehicle: Vehicle?, val error: String?) : LoadingResult()
    }

    suspend fun getVehicleById(id: UUID): LoadingResult? {
        return try {
            val response = vehicleService.getVehicle(id)
            if (response.isSuccessful){
                if (response.body() == null) {
                    return LoadingResult.SingleEntity(null, "Vehicle not found")
                } else {
                    return LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                return LoadingResult.SingleEntity(null, response.errorBody().toString())
            }
        } catch (e: Exception){
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getVehicles(size: Int = 20): LoadingResult {
        return try {
            val response = vehicleService.getVehicles()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.raw().message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")

            }
        }

    }

    suspend fun createVehicle(vehicle: NewVehicle): LoadingResult {
        return try {
            val response = vehicleService.createVehicle(vehicle)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    return LoadingResult.SingleEntity(null, "Vehicle not found")
                } else {
                    LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                LoadingResult.Error(response.raw().message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }
    suspend fun updateVehicle(id: UUID, vehicle: NewVehicle): LoadingResult {
        println(vehicle)
        return try {
            val response = vehicleService.updateVehicle(id, vehicle )

            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.SingleEntity(it, null)
                } ?: LoadingResult.SingleEntity(null, "Update failed")
            } else {
                LoadingResult.Error(response.errorBody()?.string() ?: "Update failed")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Update error")
            }
        }
    }

    suspend fun deleteVehicle(id: UUID): LoadingResult {
        return try {
            val response = vehicleService.deleteVehicle(id)
            if (response.isSuccessful) {
                LoadingResult.Success(emptyList()) // Successful deletion
            } else {
                LoadingResult.Error(response.errorBody()?.string() ?: "Deletion failed")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Deletion error")
            }
        }
    }

    suspend fun searchVehicles(
        make: String? = null,
        model: String? = null,
        regNumber: String? = null,
        clientId: UUID? = null
    ): LoadingResult {
        return try {
            val response = vehicleService.searchVehicles(make, model, regNumber, clientId)
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.errorBody()?.string() ?: "Search failed")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Search error")
            }
        }
    }
}
