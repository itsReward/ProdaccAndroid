package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.NewServiceChecklist
import com.prodacc.data.remote.dao.ServiceChecklist
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ServiceChecklistService {
    @GET("/service-checklist/all")
    suspend fun getAllServiceChecklists(): Response<List<ServiceChecklist>>

    @GET("/service-checklist/get/jobCard/{id}")
    suspend fun getServiceChecklistByJobCard(@Path("id") jobCardId: UUID): Response<ServiceChecklist>

    @GET("/service-checklist/get-by-id/{id}")
    suspend fun getServiceChecklistById(@Path("id") id: UUID): Response<ServiceChecklist>

    @POST("/service-checklist/new")
    suspend fun createServiceChecklist(@Body serviceChecklist: NewServiceChecklist): Response<ServiceChecklist>

    @PUT("/service-checklist/update/{id}")
    suspend fun updateServiceChecklist(
        @Path("id") id: UUID,
        @Body serviceChecklist: NewServiceChecklist
    ): Response<ServiceChecklist>

    @DELETE("/service-checklist/delete/{id}")
    suspend fun deleteServiceChecklist(@Path("id") id: UUID): Response<Map<String, String>>
}