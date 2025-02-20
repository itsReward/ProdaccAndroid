package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.JobCardTechnician
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobCardTechnicianRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
){
    private val service get() = apiServiceContainer.jobCardTechniciansService

    suspend fun getJobCardTechnicians(jobCardId: UUID): LoadingResult = withContext(dispatcher.io){
        try {
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

    suspend fun getJobCardsForTechnician(technicianId: UUID): LoadingResult = withContext(dispatcher.io){
        try {
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

    suspend fun addTechnicianToJobCard(jobCardTechnician: JobCardTechnician): LoadingResult = withContext(dispatcher.io){
        try {
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

    suspend fun removeTechnician(jobCardTechnician: JobCardTechnician): LoadingResult = withContext(dispatcher.io){
        try {
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