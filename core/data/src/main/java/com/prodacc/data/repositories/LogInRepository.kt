package com.prodacc.data.repositories

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.TokenManager
import com.prodacc.data.remote.dao.Token
import com.prodacc.data.remote.dao.UserLogInDetails
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogInRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val tokenManager: TokenManager,
    private val gson: Gson,
    private val dispatcher: CoroutineDispatchers
){
    private val loginService get() =  apiServiceContainer.logInService
    private val logger = Logger.getLogger(LogInRepository::class.java.name)

    sealed class LoginResult {
        data class Success(val token: Token) : LoginResult()
        data class Error(val message: ErrorMessage?) : LoginResult()
        data class ErrorSingleMessage(val message: String): LoginResult()
        data object NetworkError : LoginResult()
    }

    suspend fun login(username: String, password: String): LoginResult = withContext(dispatcher.io) {
        logger.info("LOGGING IN...")
        try {
            val loginDetails = UserLogInDetails(username, password)

            if (username.isBlank() || password.isBlank()) {
                LoginResult.ErrorSingleMessage("Username or password cannot be empty")
            }
            val response = loginService.logIn(loginDetails)


            if (response.isSuccessful) {
                response.body()?.let { token ->
                    tokenManager.saveToken(token)
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

}

data class ErrorMessage(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("detail")
    val detail : String
)