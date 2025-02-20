package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.NewJobCard
import com.prodacc.data.remote.dao.NewJobCardStatus
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobCardRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
){
    private val jobCardService get() = apiServiceContainer.jobCardService
    private val jobCardStatusService get() = apiServiceContainer.jobCardStatusService


    sealed class LoadingResult {
        data class Success(val jobCards: List<JobCard>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val jobCard: JobCard) : LoadingResult()
    }


    suspend fun newJobCard(newJobCard: NewJobCard): LoadingResult = withContext(dispatcher.io){
        try {
            val response = jobCardService.createJobCard(newJobCard)
            when {
                response.isSuccessful -> {

                        try {
                            val statusResponse = jobCardStatusService.addNewJobCardStatus(
                                NewJobCardStatus(response.body()!!.id, "opened")
                            )
                            if (statusResponse.isSuccessful) {
                                LoadingResult.SingleEntity(response.body()!!)
                            } else {
                                LoadingResult.Error("Failed to create job card status: ${statusResponse.errorBody()?.string()}")
                            }
                        } catch (e: Exception) {
                            LoadingResult.Error("## ${e.message}")
                        }
                }
                else -> {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    LoadingResult.Error("Server error: $errorBody")
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error("Network error: ${e.message}")
            }
        }
    }

    suspend fun getJobCards(): LoadingResult = withContext(dispatcher.io){
        try {

            val jobCards = jobCardService.getJobCards()



            if (jobCards.isSuccessful){
                LoadingResult.Success(jobCards.body()?: emptyList())
            } else {
                LoadingResult.Error(jobCards.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> {
                    LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
                }
            }
        }



    }
    suspend fun getJobCard(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = jobCardService.getJobCard(id)
            if (response.isSuccessful){
                response.body()?.let { LoadingResult.SingleEntity(it) } ?: LoadingResult.Error("Server returned null")
            } else {
                LoadingResult.Error("Unknown Error")
            }
        } catch (e: Exception){
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }
        }

    }

    suspend fun updateJobCard(id: UUID, jobCard: JobCard): LoadingResult = withContext(dispatcher.io){
        try {
            val response = jobCardService.updateJobCard(id, jobCard)
            println(response.code())
            println(response.body())

            if (response.isSuccessful){

                response.body()?.let { LoadingResult.SingleEntity(it) }?: LoadingResult.Error("Server returned null")
            } else {
                LoadingResult.Error("Unknown Error")
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message?:"Unknown Error" )
            }
        }
    }

    suspend fun deleteJobCard(id: UUID) : String = withContext(dispatcher.io){
        try {
            val response = jobCardService.deleteJobCard(id)
            if (response.isSuccessful){
                response.body()?.let { "deleted successfully" } ?: "Server returned null"
            } else {
                "Unknown Error"
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> "Network Error"
                else -> "Unknown Error"
            }
        }
    }
}
