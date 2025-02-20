package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.JobCardStatus
import com.prodacc.data.remote.dao.NewJobCardStatus
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobCardStatusRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
){
    private val service get() = apiServiceContainer.jobCardStatusService

    suspend fun addNewJobCardStatus(jobCardId: UUID, status: String): LoadingResult = withContext(dispatcher.io){
        try {
            val jobCardStatus = NewJobCardStatus(
                jobCardId,
                status,
                LocalDateTime.now()
            )
            val response = service.addNewJobCardStatus(jobCardStatus)

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

    suspend fun getJobCardStatusesByJobId(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
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
