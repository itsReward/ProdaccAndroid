package com.prodacc.data.repositories

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.dao.Token
import com.prodacc.data.remote.dao.UserLogInDetails
import java.io.IOException
import java.util.logging.Logger

class LogInRepository {
    private var loginService = ApiInstance.logInService
    private val logger = Logger.getLogger(LogInRepository::class.java.name)
    private val gson = Gson()

    sealed class LoginResult {
        data class Success(val token: Token) : LoginResult()
        data class Error(val message: ErrorMessage?) : LoginResult()
        data class ErrorSingleMessage(val message: String): LoginResult()
        data object NetworkError : LoginResult()
    }

    suspend fun login(username: String, password: String): LoginResult {
        logger.info("LOGGING IN...")
        return try {
            val loginDetails = UserLogInDetails(username, password)

            if (username.isBlank() || password.isBlank()) {
                return LoginResult.ErrorSingleMessage("Username or password cannot be empty")
            }
            val response = loginService.logIn(loginDetails)


            if (response.isSuccessful) {
                response.body()?.let { token ->
                    TokenManager.saveToken(token)
                    LoginResult.Success(token)
                } ?: LoginResult.ErrorSingleMessage("Empty response body")
            } else {
                logger.info("Error connecting")
                println("####Error####")
                println(response.errorBody()?.string())

                LoginResult.Error(gson.fromJson(response.errorBody()?.string(), ErrorMessage::class.java))
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoginResult.NetworkError
                else -> LoginResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun reinitializeService(){
        loginService = ApiInstance.logInService
    }
}

data class ErrorMessage(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("detail")
    val detail : String
)