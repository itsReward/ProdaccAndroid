package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardTechnician
import com.prodacc.data.remote.services.JobCardTechniciansService
import java.io.IOException
import java.util.UUID

class JobCardTechnicianRepository {
    private val service = ApiInstance.jobCardTechniciansService

    suspend fun getJobCardTechnicians(jobCardId: UUID): LoadingResult {
        println("###############getting jobcard technicians")
        return try {
            val response = service.getTechniciansForJobCard(jobCardId)

            println( "URL: " + response.raw().request().url())
            println("Request Header: " +response.raw().request().headers())
            println("Method: " +response.raw().request().method())
            println("Response Body: " +response.body())

            if (response.isSuccessful) {
                LoadingResult.Success(response.body()!!)
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }
        }
    }

    suspend fun getJobCardsForTechnician(technicianId: UUID): LoadingResult {
        return try {
            val response = service.getJobCardsForTechnician(technicianId)
            if (response.isSuccessful) {
                LoadingResult.Success(response.body()!!)
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }

        }
    }

    suspend fun addTechnicianToJobCard(jobCardTechnician: JobCardTechnician): LoadingResult {
        return try {
            println("###############adding technician")
            val response = service.addTechnicianToJobCard(jobCardTechnician)


            println("URL: " +response.raw().request().url())
            println("Body: " +response.raw().request().body())
            println("Method: " +response.raw().request().method())

            println("Response Body: " +response.body())

            if (response.isSuccessful) {
                LoadingResult.Success(listOf(response.body()!!.technicianId))
            } else {
                LoadingResult.Error(response.message())
            }
        }catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val list: List<UUID>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data object Loading : LoadingResult()
    }
}