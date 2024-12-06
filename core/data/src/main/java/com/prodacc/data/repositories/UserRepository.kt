package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.NewUser
import com.prodacc.data.remote.dao.User
import java.io.IOException
import java.util.UUID

class UserRepository {
    private val service = ApiInstance.userService

    suspend fun getAllUsers(): LoadingResult {
        return try {
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

    suspend fun getUserById(id: UUID): LoadingResult {
        return try {
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

    suspend fun getUserByUsername(username: String): LoadingResult {
        return try {
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

    suspend fun getUserByEmail(email: String): LoadingResult {
        return try {
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

    suspend fun getUserByEmployeeId(employeeId: UUID): LoadingResult {
        return try {
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

    suspend fun createUser(newUser: NewUser): LoadingResult {
        return try {
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

    suspend fun updateUser(id: UUID, userUpdate: NewUser): LoadingResult {
        return try {
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

    suspend fun deleteUser(id: UUID): LoadingResult {
        return try {
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