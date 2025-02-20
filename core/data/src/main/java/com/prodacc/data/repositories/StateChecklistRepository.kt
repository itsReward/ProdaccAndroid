package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.dao.NewStateChecklist
import com.prodacc.data.remote.dao.StateChecklist
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StateChecklistRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val webSocketInstance: WebSocketInstance,
    private val dispatcher: CoroutineDispatchers
){
    val service get() = apiServiceContainer.stateChecklistService

    suspend fun getStateChecklist(id: UUID): LoadingResults = withContext(dispatcher.io){
        try {
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

    suspend fun newStateChecklist(checklist: NewStateChecklist): LoadingResults = withContext(dispatcher.io){
        try {
            val response = service.createStateChecklist(checklist)
            if (response.isSuccessful){
                webSocketInstance.sendWebSocketMessage("NEW_STATE_CHECKLIST", checklist.jobCardId.toString())
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

    suspend fun updateStateChecklist(id: UUID, checklist: NewStateChecklist): LoadingResults = withContext(dispatcher.io){
        try {
            val response = service.updateStateChecklist(id, checklist)
            if (response.isSuccessful){
                webSocketInstance.sendWebSocketMessage("UPDATE_STATE_CHECKLIST", checklist.jobCardId.toString())
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