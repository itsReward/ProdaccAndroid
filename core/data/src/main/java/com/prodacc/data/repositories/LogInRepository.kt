package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.Token
import com.prodacc.data.remote.dao.UserLogInDetails
import java.io.IOException

class LogInRepository {
    private val loginService = ApiInstance.logInService

    sealed class LoginResult {
        data class Success(val token: Token) : LoginResult()
        data class Error(val message: String) : LoginResult()
        data object NetworkError : LoginResult()
    }

    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val loginDetails = UserLogInDetails(username, password)
            val response = loginService.logIn(loginDetails)

            if (response.isSuccessful) {
                response.body()?.let { token ->
                    LoginResult.Success(token)
                } ?: LoginResult.Error("Empty response body")
            } else {
                LoginResult.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoginResult.NetworkError
                else -> LoginResult.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}