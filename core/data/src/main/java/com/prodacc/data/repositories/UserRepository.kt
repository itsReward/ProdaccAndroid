package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.NewUser
import com.prodacc.data.remote.dao.User
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
){
    private val service get() = apiServiceContainer.userService

    suspend fun getAllUsers(): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getAllUsers()
            if (response.isSuccessful) {
                println("Response Code: ${response.code()}")
                println("Response Raw: ${response.raw()}")
                println("Response Headers: ${response.headers()}")
                LoadingResult.Success(response.body()!!)
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getUserById(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getUserById(id)
            println(response.body())
            if (response.isSuccessful) {
                println(response.body()!!)
                if (response.body() != null) {
                    LoadingResult.UserEntity(response.body()!!)
                } else {
                    LoadingResult.Error("Returned Empty Body")
                }
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getUserByUsername(username: String): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getUserByUsername(username)
            println(response)
            if (response.isSuccessful) {
                response.body()?.let {
                    println(it)
                    LoadingResult.UserEntity(it)
                } ?: LoadingResult.Error("Returned Empty Body")
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getUserByEmail(email: String): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getUserByEmail(email)
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.UserEntity(it)
                } ?: LoadingResult.Error("Returned Empty Body")
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getUserByEmployeeId(employeeId: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getUserByEmployeeId(employeeId)
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.UserEntity(it)
                } ?: LoadingResult.Error("Returned Empty Body")
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun createUser(newUser: NewUser): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.createUser(newUser)
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.Success(listOf(it))
                } ?: LoadingResult.Error("Empty response body")
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun updateUser(id: UUID, userUpdate: NewUser): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.updateUser(id, userUpdate)
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.UserEntity(it)
                } ?: LoadingResult.Error("Empty response body")
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun deleteUser(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.deleteUser(id)
            println(response.raw())
            if (response.isSuccessful) {
                LoadingResult.Success()
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val users: List<User>? = null) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data object NetworkError : LoadingResult()
        data class UserEntity(val user: User) : LoadingResult()
    }
}