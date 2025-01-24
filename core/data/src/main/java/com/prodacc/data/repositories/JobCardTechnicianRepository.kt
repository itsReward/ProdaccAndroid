package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardTechnician
import com.prodacc.data.remote.services.JobCardTechniciansService
import java.io.IOException
import java.util.UUID

class JobCardTechnicianRepository {
    private val service = ApiInstance.jobCardTechniciansService

    suspend fun getJobCardTechnicians(jobCardId: UUID): LoadingResult {
        return try {
            val response = service.getTechniciansForJobCard(jobCardId)

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
            val response = service.addTechnicianToJobCard(jobCardTechnician)

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

    suspend fun removeTechnician(jobCardTechnician: JobCardTechnician): LoadingResult {
        return try {
            val response = service.removeTechnicianFromJobCard(jobCardTechnician)

            if (response.isSuccessful) {
                LoadingResult.Success(emptyList())
            } else {
                LoadingResult.Error(response.errorBody()?.string()?:"Unknown Error")
            }
        }catch (e: Exception){
            when (e){
                is IOException -> {
                    LoadingResult.Error("Network Error")
                }
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