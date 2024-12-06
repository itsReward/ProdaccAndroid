package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.remote.dao.NewJobCardStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface JobCardStatusService {
    @GET("job-card-status/all")
    suspend fun getAllJobCardStatuses(): Response<List<JobCardStatus>>

    @GET("job-card-status/get/{id}")
    suspend fun getJobCardStatusesByJobId(@Path("id") jobId: UUID): Response<List<JobCardStatus>>

    @POST("job-card-status/newJobCardStatus")
    suspend fun addNewJobCardStatus(@Body statusRequest: NewJobCardStatus): Response<JobCardStatus>
}