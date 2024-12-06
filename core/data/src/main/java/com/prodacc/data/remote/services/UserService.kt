package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.NewUser
import com.prodacc.data.remote.dao.User
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface UserService {
    @GET("/users/all")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("/users/get/{id}")
    suspend fun getUserById(@Path("id") id: UUID): Response<User>

    @GET("/users/findByUserName/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<User>

    @GET("/users/find/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<User>

    @GET("/users/find/employee/{employeeId}")
    suspend fun getUserByEmployeeId(@Path("employeeId") employeeId: UUID): Response<User>

    @POST("/users/new")
    suspend fun createUser(@Body newUser: NewUser): Response<User>

    @PUT("/users/update/{id}")
    suspend fun updateUser(
        @Path("id") id: UUID,
        @Body userUpdate: NewUser
    ): Response<User>

    @DELETE("/users/delete/{id}")
    suspend fun deleteUser(@Path("id") id: UUID): Response<Map<String, String>>
}