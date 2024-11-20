package com.prodacc.data.repositories

import com.google.gson.Gson
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.Token
import com.prodacc.data.remote.dao.UserLogInDetails
import java.io.IOException

class LogInRepository {
    private val loginService = ApiInstance.logInService
    private val gson = Gson()

    sealed class LoginResult {
        data class Success(val token: Token) : LoginResult()
        data class Error(val message: ErrorMessage?) : LoginResult()
        data class ErrorSingleMessage(val message: String): LoginResult()
        data object NetworkError : LoginResult()
    }

    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val loginDetails = UserLogInDetails(username, password)
            println(username)
            println(password)
            val response = loginService.logIn(loginDetails)

            if (response.isSuccessful) {
                response.body()?.let { token ->
                    LoginResult.Success(token)
                } ?: LoginResult.ErrorSingleMessage("Empty response body")
            } else {
                LoginResult.Error(gson.fromJson(response.errorBody()?.string(), ErrorMessage::class.java))
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoginResult.NetworkError
                else -> LoginResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }
}

data class ErrorMessage(
    val timestamp: String,
    val message: String,
    val detail : String
)