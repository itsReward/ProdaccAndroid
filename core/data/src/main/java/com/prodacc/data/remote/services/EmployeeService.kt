package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.NewEmployee
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EmployeeService {
    @GET("/employees/all")
    suspend fun getAllEmployees(): Response<List<Employee>>

    @GET("/employees/id/{id}")
    suspend fun getEmployeeById(@Path("id") id: String): Response<Employee>

    @POST("/employees/new")
    suspend fun createEmployee(@Body employee: NewEmployee): Response<Employee>

    @PUT("/employees/update/{id}")
    suspend fun updateEmployee(@Path("id") id: String, @Body employee: NewEmployee): Response<Employee>

    @DELETE("/employees/delete/{id}")
    suspend fun deleteEmployee(@Path("id") id: String): Response<Void>





}