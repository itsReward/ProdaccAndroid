package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.JobCard
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface JobCardService {
    @GET("/jobCards/all")
    suspend fun getJobCards(): Response<List<JobCard>>

    @GET("/jobCards/get/{id}")
    suspend fun getJobCard(@Path("id") id: UUID): Response<JobCard>

    @POST("/jobCards/new")
    suspend fun createJobCard(@Body jobCard: JobCard): Response<JobCard>

    @PUT("/jobCards/update/{id}")
    suspend fun updateJobCard(@Path("id") id: UUID, @Body jobCard: JobCard): Response<JobCard>

    @DELETE("/jobCards/delete/{id}")
    suspend fun deleteJobCard(@Path("id") id: UUID): Response<String>

}