package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.Vehicle
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface VehicleService {
    @GET("/vehicles/all")
    suspend fun getVehicles(): Response<List<Vehicle>>

    @GET("/vehicles/get/{id}")
    suspend fun getVehicle(@Path("id") id: UUID): Response<Vehicle>

    @POST("/vehicles/new")
    suspend fun createVehicle(@Body vehicle: Vehicle): Response<Vehicle>

    @PUT("/vehicles/update/{id}")
    suspend fun updateVehicle(@Path("id") id: UUID, @Body vehicle: Vehicle): Response<Vehicle>

    @DELETE("/vehicles/delete/{id}")
    suspend fun deleteVehicle(@Path("id") id: UUID): Response<String>
}