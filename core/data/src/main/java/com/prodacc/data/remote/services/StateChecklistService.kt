package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.NewStateChecklist
import com.prodacc.data.remote.dao.StateChecklist
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface StateChecklistService {
    @GET("/state-checklist/all")
    suspend fun getAllStateChecklists(): Response<List<StateChecklist>>

    @GET("/state-checklist/get-by-jobCard/{id}")
    suspend fun getStateChecklistByJobCard(@Path("id") jobCardId: UUID): Response<StateChecklist>

    @GET("/state-checklist/get-by-id/{id}")
    suspend fun getStateChecklistById(@Path("id") id: UUID): Response<StateChecklist>

    @POST("/state-checklist/new")
    suspend fun createStateChecklist(@Body stateChecklist: NewStateChecklist): Response<StateChecklist>

    @PUT("/state-checklist/update/{id}")
    suspend fun updateStateChecklist(
        @Path("id") id: UUID,
        @Body stateChecklist: NewStateChecklist
    ): Response<StateChecklist>

    @DELETE("/state-checklist/delete/{id}")
    suspend fun deleteStateChecklist(@Path("id") id: UUID): Response<Map<String, String>>
}