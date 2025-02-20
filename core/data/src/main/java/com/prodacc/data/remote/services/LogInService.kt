package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.Token
import com.prodacc.data.remote.dao.UserLogInDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LogInService {
    @POST("/auth")
    suspend fun logIn(
        @Body request: UserLogInDetails
    ): Response<Token>

}