package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.NewServiceChecklist
import com.prodacc.data.remote.dao.ServiceChecklist
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class ServiceChecklistRepository {
    private val service = ApiInstance.serviceChecklistService

    suspend fun getServiceChecklistByJobCard(id: UUID): LoadingResult {
        return try {
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


    suspend fun addServiceChecklist(serviceChecklist: NewServiceChecklist): LoadingResult {
        return try {
            val response = service.createServiceChecklist(serviceChecklist)
            if (response.isSuccessful){
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


    suspend fun updateServiceChecklist(id: UUID, serviceChecklist: NewServiceChecklist): LoadingResult {
        return try {
            val response = service.updateServiceChecklist(id, serviceChecklist)
            if (response.isSuccessful){
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