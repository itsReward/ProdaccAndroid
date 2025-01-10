package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.JobCardTechnician
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface JobCardTechniciansService {
    @GET("job-card-technicians/allEntities")
    suspend fun getAllJobCardTechnicians(): Response<List<JobCardTechnician>>

    @GET("job-card-technicians/getAllJobCardTechnicians/{id}")
    suspend fun getTechniciansForJobCard(@Path("id") jobCardId: UUID): Response<List<UUID>>

    @GET("job-card-technicians/getJobCardsByTechnician/{id}")
    suspend fun getJobCardsForTechnician(@Path("id") technicianId: UUID): Response<List<UUID>>

    @POST("job-card-technicians/add-technician")
    suspend fun addTechnicianToJobCard(@Body jobCardTechnician: JobCardTechnician): Response<JobCardTechnician>

    @HTTP(method = "DELETE", path = "job-card-technicians/remove-technician", hasBody = true)
    suspend fun removeTechnicianFromJobCard(@Body jobCardTechnician: JobCardTechnician): Response<String>
}