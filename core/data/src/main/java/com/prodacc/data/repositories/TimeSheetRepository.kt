package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.NewTimesheet
import com.prodacc.data.remote.dao.Timesheet
import com.prodacc.data.remote.dao.UpdateTimesheet
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class TimeSheetRepository {
    private val service = ApiInstance.timesheetService

    suspend fun getTimeSheets(): LoadingResult {
        return try {
            val response = service.getAllTimesheets()
            if (response.isSuccessful){
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getJobCardTimeSheets(jobCardId: UUID): LoadingResult {
        return try {
            val response = service.getTimesheetsByJobCardId(jobCardId)
            if (response.isSuccessful){
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getTimeSheet(id: UUID): LoadingResult {
        return try{
            val response = service.getTimesheetById(id)
            if (response.isSuccessful){
                if (response.body() == null){
                    LoadingResult.Error("No TimeSheet Response")
                } else {
                    LoadingResult.TimeSheet(response.body()!!)
                }

            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }


    suspend fun addTimeSheet(timesheet: NewTimesheet): LoadingResult {
        return try {
            println("Request URL: ${service.addTimesheet(timesheet).raw().request().url()}")
            println("Request Body: $timesheet")
            println("Headers: ${service.addTimesheet(timesheet).raw().request().headers()}")

            val response = service.addTimesheet(timesheet)

            println("Response Code: ${response.code()}")
            println("Response Message: ${response.message()}")
            println("Response Body: ${response.errorBody()?.string()}")

            if (response.isSuccessful){
                if (response.body() == null){
                    LoadingResult.Error("No TimeSheet Response")
                } else {
                    LoadingResult.TimeSheet(response.body()!!)
                }
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception){

            println("Exception: ${e.message}")
            println("Stack trace: ${e.stackTrace}")

            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun updateTimesheet(id: UUID, timesheet: UpdateTimesheet): LoadingResult {
        return try {
            val response = service.updateTimesheet(id, timesheet)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.Error("No TimeSheet Response")
                } else {
                    LoadingResult.TimeSheet(response.body()!!)
                }
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun deleteTimesheet(id: UUID): LoadingResult {
        return try {
            val response = service.deleteTimesheet(id)
            if (response.isSuccessful) {
                LoadingResult.Message("Timesheet Deleted")
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    sealed class LoadingResult{
        data class Success(val timesheets: List<Timesheet>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data class TimeSheet(val timesheet: Timesheet) : LoadingResult()
        data class Message(val message: String) : LoadingResult()
    }
}