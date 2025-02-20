package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.NewEmployee
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
) {
    private val service get() = apiServiceContainer.employeeService

    suspend fun getEmployees(): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getAllEmployees()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body()!!)
            } else {
                LoadingResult.Error(response.message())
            }
        }catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message?:"Unknown Error")

            }
        }
    }

    suspend fun getEmployee(id: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getEmployeeById(id)
            if (response.isSuccessful) {

                if (response.body() != null) {

                    LoadingResult.EmployeeEntity(response.body()!!)
                } else {
                    LoadingResult.Error("Returned Empty Body")
                }
            } else {
                LoadingResult.Error(response.message())
            }
        }catch (e: Exception){
            when (e){
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.Error(e.message?:"Unknown Error")
            }
        }
    }


    suspend fun updateEmployee(id: UUID, employee: NewEmployee): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.updateEmployee(id, employee)
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadingResult.EmployeeEntity(it)
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

    suspend fun deleteEmployee(id: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.deleteEmployee(id.toString())
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

    suspend fun createEmployee(newEmployee: NewEmployee): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.createEmployee(newEmployee)
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


    sealed class LoadingResult {
        data class Success(val employees: List<Employee>? = null) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data object NetworkError : LoadingResult()
        data class EmployeeEntity(val employee: Employee) : LoadingResult()
    }

}
