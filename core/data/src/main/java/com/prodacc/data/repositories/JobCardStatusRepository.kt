package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.remote.dao.NewJobCardStatus
import com.prodacc.data.remote.services.JobCardStatusService
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class JobCardStatusRepository {
    private val service = ApiInstance.jobCardStatusService

    private val status =
        listOf("opened", "diagnostics", "approval", "work in progress", "testing", "done", "frozen")


    suspend fun addNewJobCardStatus(jobCardId: UUID, status: String): LoadingResult{
        return try {
            val jobCardStatus = NewJobCardStatus(
                jobCardId,
                status,
                LocalDateTime.now()
            )
            val response = service.addNewJobCardStatus(jobCardStatus)


            println("URL:" + response.raw().request().url())
            println("Request Body:" + response.raw().request().body())
            println("Response Code:" + response.code())
            println("Response Body:" +response.body())
            println("Response Error Body:" + (response.errorBody()?: response.message()) )

            if (response.isSuccessful){
                when (val list = getJobCardStatusesByJobId(jobCardId)){
                    is LoadingResult.Error -> LoadingResult.Error(list.message)
                    is LoadingResult.Loading -> LoadingResult.Loading
                    is LoadingResult.Success -> LoadingResult.Success(list.status)
                }
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e:Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }
        }
    }

    suspend fun getJobCardStatusesByJobId(id: UUID): LoadingResult {
        return try {
            val response = service.getJobCardStatusesByJobId(id)
            if (response.isSuccessful) {
                LoadingResult.Success(response.body()!!)
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

    sealed class LoadingResult{
        data class Success(val status: List<JobCardStatus>): LoadingResult()
        data class Error(val message: String): LoadingResult()
        data object Loading: LoadingResult()

    }
}