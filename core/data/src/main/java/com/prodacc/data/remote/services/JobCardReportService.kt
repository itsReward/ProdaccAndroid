package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.remote.dao.NewJobCardReport
import com.prodacc.data.remote.dao.UpdateJobCardReport
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface JobCardReportService {
    @GET("/job-card-reports/getReportById/{id}")
    suspend fun getReportById(@Path("id") id: UUID): Response<JobCardReport>

    @GET("/job-card-reports/getJobCardReports/{id}")
    suspend fun getJobCardReports(@Path("id") id: UUID): Response<List<JobCardReport>>

    @GET("/job-card-reports/getAllJobCardReports/{id}")
    suspend fun getAllJobCardReports(@Path("id") id: UUID): Response<List<JobCardReport>>

    @POST("/job-card-reports/new")
    suspend fun createReport(@Body reportRequest: NewJobCardReport): Response<JobCardReport>

    @PUT("/job-card-reports/update/{id}")
    suspend fun updateReport(
        @Path("id") id: UUID,
        @Body reportRequest: UpdateJobCardReport
    ): Response<JobCardReport>

    @DELETE("/job-card-reports/delete/{id}")
    suspend fun deleteReport(@Path("id") id: UUID): Response<Unit>
}