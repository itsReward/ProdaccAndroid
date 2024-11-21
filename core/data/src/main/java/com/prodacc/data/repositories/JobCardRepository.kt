package com.prodacc.data.repositories;

import com.google.gson.Gson
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.dao.JobCard;

import java.util.UUID

class JobCardRepository {
    private val jobCardService = ApiInstance.jobCardService
    private val gson = Gson()



    suspend fun getJobCards(size : Int = 15 ): kotlin.collections.List<JobCard> {
        println(TokenManager.getToken()?.token) //this thing is null, solve it
        val jobCards = jobCardService.getJobCards()
        if (jobCards.isSuccessful){
            val jobs =  jobCards.body()?.map { gson.fromJson(it.toString(), JobCard::class.java) }
            return jobs?: emptyList()
        } else {
            println(jobCards.errorBody())
            return emptyList()
        }
    }
    suspend fun getJobCard(id: UUID): JobCard? {
        val jobCard =  jobCardService.getJobCard(id)
        return if (jobCard.isSuccessful){
            gson.fromJson(jobCard.toString(), JobCard::class.java)
        } else {
            null
        }
    }
}
