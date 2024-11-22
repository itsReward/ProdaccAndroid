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
        data class Error(val message: ResponseBody?) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
    }


    suspend fun getJobCards(): LoadingResult {
        return try {
            val jobCards = jobCardService.getJobCards()
            if (jobCards.isSuccessful){

                LoadingResult.Success(jobCards.body()?: emptyList())
            } else {
                println(jobCards.errorBody())
                return LoadingResult.Error(jobCards.errorBody())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }



    }
    suspend fun getJobCard(id: UUID): JobCard? {
        val jobCard =  jobCardService.getJobCard(id)
        return if (jobCard.isSuccessful){
           return jobCard.body()
        } else {
            null
        }
    }
}
