package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.NewTimesheet
import com.prodacc.data.remote.dao.Timesheet
import com.prodacc.data.remote.dao.UpdateTimesheet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface TimesheetService {
    @GET("timesheets/all")
    suspend fun getAllTimesheets(): Response<List<Timesheet>>

    @GET("timesheets/jobCard/{id}")
    suspend fun getTimesheetsByJobCardId(@Path("id") jobCardId: UUID): Response<List<Timesheet>>

    @GET("timesheets/get/{id}")
    suspend fun getTimesheetById(@Path("id") timesheetId: UUID): Response<Timesheet>

    @POST("timesheets/add")
    suspend fun addTimesheet(@Body timesheetRequest: NewTimesheet): Response<Timesheet>

    @PUT("timesheets/update/{id}")
    suspend fun updateTimesheet(
        @Path("id") timesheetId: UUID,
        @Body timesheetRequest: Timesheet
    ): Response<Timesheet>

    @DELETE("timesheets/delete/{id}")
    suspend fun deleteTimesheet(@Path("id") timesheetId: UUID): Response<String>
}