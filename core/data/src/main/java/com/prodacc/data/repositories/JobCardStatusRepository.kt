package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.remote.services.JobCardStatusService
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class JobCardStatusRepository {
    private val service = ApiInstance.jobCardStatusService

    private val status =
        listOf("opened", "diagnostics", "approval", "work in progress", "testing", "done", "frozen")

    fun generateJobCardStatus(id: UUID): List<JobCardStatus> {
        return List(6) { index ->
            JobCardStatus(
                statusId = UUID.randomUUID(),
                jobId = id,
                status = status[index],
                createdAt = LocalDateTime.now().plusHours(index.toLong())
            )
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