package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.NewClient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface ClientService {
    @GET("/clients/all")
    suspend fun getClients(): Response<List<Client>>

    @GET("/clients/get/{id}")
    suspend fun getClient(@Path("id") id: UUID): Response<Client>

    @POST("/clients/new")
    suspend fun createClient(@Body client: NewClient): Response<Client>

    @PUT("/clients/update/{id}")
    suspend fun updateClient(@Path("id") id: UUID, @Body client: Client): Response<Client>

    @DELETE("/clients/delete/{id}")
    suspend fun deleteClient(@Path("id") id: UUID): Response<Unit>

}