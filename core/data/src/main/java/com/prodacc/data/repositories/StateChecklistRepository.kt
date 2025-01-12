package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.NewStateChecklist
import com.prodacc.data.remote.dao.StateChecklist
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

class StateChecklistRepository {
    val service = ApiInstance.stateChecklistService

    suspend fun getStateChecklist(id: UUID): LoadingResults {
        return try {
            val response = service.getStateChecklistByJobCard(id)

            if (response.isSuccessful){
                LoadingResults.Success(response.body()!!)
            } else if (response.code() == 500){
                LoadingResults.Success(null)
            } else {
                LoadingResults.Success(null)
            }
        } catch (e: Exception) {
            when (e){
                is IOException -> LoadingResults.Error("Network Error")
                else -> LoadingResults.Error(e.message?:"Unknown Error")
            }
        }
    }

    suspend fun newStateChecklist(checklist: NewStateChecklist): LoadingResults {
        return try {
            val response = service.createStateChecklist(checklist)
            if (response.isSuccessful){
                LoadingResults.Success(response.body()!!)
            } else {
                LoadingResults.Error(response.message())
            }
        } catch (e: Exception) {
            when (e){
                is IOException -> LoadingResults.Error("Network Error")
                else -> LoadingResults.Error(e.message?:"Unknown Error")
            }
        }
    }

    suspend fun updateStateChecklist(id: UUID, checklist: NewStateChecklist): LoadingResults {
        return try {
            val response = service.updateStateChecklist(id, checklist)
            if (response.isSuccessful){
                LoadingResults.Success(response.body()!!)
            } else {
                LoadingResults.Error(response.message())
            }
        } catch (e: Exception) {
            when (e){
                is IOException -> LoadingResults.Error("Network Error")
                else -> LoadingResults.Error(e.message?:"Unknown Error")
            }
        }
    }


    sealed class LoadingResults{
        data class Success(val stateChecklist: StateChecklist?): LoadingResults()
        data class Error(val message: String): LoadingResults()
        data object Loading: LoadingResults()
    }
}