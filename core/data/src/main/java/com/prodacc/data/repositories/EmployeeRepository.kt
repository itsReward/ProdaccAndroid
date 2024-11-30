package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.EmployeeJobCard
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.remote.dao.NewEmployee
import java.io.IOException
import java.util.UUID

class EmployeeRepository {
    val service = ApiInstance.employeeService

    suspend fun getEmployees(): LoadingResult {
        return try {
            val response = service.getAllEmployees()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
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

    suspend fun getEmployee(id: UUID): LoadingResult{
        return try {
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

    sealed class LoadingResult {
        data class Success(val employees: List<Employee>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data object NetworkError : LoadingResult()
        data class EmployeeEntity(val employee: Employee) : LoadingResult()
    }
}