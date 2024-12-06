package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.ControlChecklist
import com.prodacc.data.remote.dao.NewControlChecklist
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDateTime
import java.util.UUID

interface ControlChecklistService {
    @GET("/control-checklist/all")
    suspend fun getAllControlChecklists(): Response<List<ControlChecklist>>

    @GET("/control-checklist/get-by-jobCard/{id}")
    suspend fun getControlChecklistByJobCard(@Path("id") jobCardId: UUID): Response<ControlChecklist>

    @GET("/control-checklist/get-by-id/{id}")
    suspend fun getControlChecklistById(@Path("id") id: UUID): Response<ControlChecklist>

    @POST("/control-checklist/new")
    suspend fun createControlChecklist(@Body controlChecklist: NewControlChecklist): Response<ControlChecklist>

    @PUT("/control-checklist/update/{id}")
    suspend fun updateControlChecklist(
        @Path("id") id: UUID,
        @Body controlChecklist: NewControlChecklist
    ): Response<ControlChecklist>

    @DELETE("/control-checklist/delete/{id}")
    suspend fun deleteControlChecklist(@Path("id") id: UUID): Response<Map<String, String>>
}
