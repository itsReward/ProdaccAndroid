package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.ControlChecklist
import com.prodacc.data.remote.dao.NewControlChecklist
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class ControlChecklistRepository {
    private val service = ApiInstance.controlChecklistService

    suspend fun getControlChecklist(jobCardId: UUID): LoadingResult {
        return try {
            val response = service.getControlChecklistByJobCard(jobCardId = jobCardId)

            if (response.isSuccessful){
                println("response body: ${response.body()}")

                LoadingResult.Success(response.body()!!)
            }else if(
                response.code() == 404
            ){
                LoadingResult.Success(null)
            } else {
                LoadingResult.Error(response.message())
            }

        } catch (e: Exception){
            println(e.message)

            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun addControlChecklist(controlChecklist: NewControlChecklist): LoadingResult {
        return try {
            val response = service.createControlChecklist(controlChecklist)
            if (response.isSuccessful){
                WebSocketInstance.sendWebSocketMessage("NEW_CONTROL_CHECKLIST", controlChecklist.jobCardId)
                LoadingResult.Success(response.body()!!)
            }else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun updateControlChecklist(id : UUID, controlChecklist: NewControlChecklist): LoadingResult {
        return try {
            val response = service.updateControlChecklist(id , controlChecklist)
            if (response.isSuccessful){
                WebSocketInstance.sendWebSocketMessage("UPDATE_CONTROL_CHECKLIST", controlChecklist.jobCardId)
                LoadingResult.Success(response.body()!!)
            }else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }


    sealed class LoadingResult{
        data object Loading : LoadingResult()
        data class Success(val data: ControlChecklist?) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
    }
}