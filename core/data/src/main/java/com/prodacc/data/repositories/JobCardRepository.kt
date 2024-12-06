package com.prodacc.data.repositories;

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCard;
import okhttp3.ResponseBody
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.UUID

class JobCardRepository {
    private val jobCardService = ApiInstance.jobCardService
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            // Adjust the parsing format to match your actual JSON
            val dateString = json.asString
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            // Or use a custom formatter if the date format is different
            // LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        })
        .create()

    sealed class LoadingResult {
        data class Success(val jobCards: List<JobCard>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val jobCard: JobCard) : LoadingResult()
    }

    //data class JobCardResult(val jobCard: JobCard):LoadingResult()

    suspend fun getJobCards(): LoadingResult {
        return try {
            val jobCards = jobCardService.getJobCards()
            if (jobCards.isSuccessful){
                LoadingResult.Success(jobCards.body()?: emptyList())
            } else {
                println(jobCards.errorBody())
                return LoadingResult.Error(jobCards.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }



    }
    suspend fun getJobCard(id: UUID): LoadingResult {
        return try {
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
}
