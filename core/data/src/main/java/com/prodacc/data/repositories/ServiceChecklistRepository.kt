package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.NewServiceChecklist
import com.prodacc.data.remote.dao.ServiceChecklist
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceChecklistRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val webSocketInstance: WebSocketInstance,
    private val dispatcher: CoroutineDispatchers
){
    private val service get() = apiServiceContainer.serviceChecklistService

    suspend fun getServiceChecklistByJobCard(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getServiceChecklistByJobCard(id)

            println(response.raw().request.url)
            println(response.raw().code)
            println(response.raw().body)

            if (response.isSuccessful){
                LoadingResult.Success(response.body())
            } else if (response.code()==500){
                LoadingResult.Success(null)
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


    suspend fun addServiceChecklist(serviceChecklist: NewServiceChecklist): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.createServiceChecklist(serviceChecklist)
            if (response.isSuccessful){
                webSocketInstance.sendWebSocketMessage("NEW_SERVICE_CHECKLIST", serviceChecklist.jobCardId.toString())
                LoadingResult.Success(response.body())
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


    suspend fun updateServiceChecklist(id: UUID, serviceChecklist: NewServiceChecklist): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.updateServiceChecklist(id, serviceChecklist)
            if (response.isSuccessful){
                webSocketInstance.sendWebSocketMessage("UPDATE_SERVICE_CHECKLIST", serviceChecklist.jobCardId.toString())
                LoadingResult.Success(response.body())
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



    sealed class LoadingResult{
        data object Loading: LoadingResult()
        data class Error(val message: String): LoadingResult()
        data class Success(val data : ServiceChecklist?): LoadingResult()
    }

}