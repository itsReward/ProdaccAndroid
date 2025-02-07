package com.prodacc.data

import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.User
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.UserRepository

object SignedInUser{
    var role: Role? = null
    var user: User? = null
    var employee: Employee? = null

    suspend fun initialize(signedIn: String): UserSignInResult {
        return when (val signedInUser = UserRepository().getUserByUsername(signedIn)){
            is UserRepository.LoadingResult.Error -> {
                UserSignInResult.Error(signedInUser.message)
            }
            is UserRepository.LoadingResult.NetworkError -> {
                UserSignInResult.Error("Network Error")
            }
            is UserRepository.LoadingResult.Success -> {
                UserSignInResult.Error("Returned List, Can never happen anyways")
            }
            is UserRepository.LoadingResult.UserEntity -> {
                user = signedInUser.user
                role = when(user!!.userRole){
                    "SERVICE_ADVISOR" -> Role.ServiceAdvisor
                    "SUPERVISOR" -> Role.Supervisor
                    "TECHNICIAN" -> Role.Technician
                    "ADMIN" -> Role.Admin
                    else -> Role.Technician
                }
                try {
                    fetchEmployee()
                }catch (e:Exception){
                    UserSignInResult.Error(e.message?: "Eish Unknown Error ")
                }

                UserSignInResult.Success
            }
        }
    }

    private suspend fun fetchEmployee(){
        when (val response = EmployeeRepository().getEmployee(user!!.employeeId)){
            is EmployeeRepository.LoadingResult.EmployeeEntity -> employee = response.employee
            is EmployeeRepository.LoadingResult.Error -> UserSignInResult.Error(response.message)
            is EmployeeRepository.LoadingResult.NetworkError -> UserSignInResult.Error("Network Error")
            is EmployeeRepository.LoadingResult.Success -> {}
        }
    }

    sealed class UserSignInResult{
        data class Error(val message: String): UserSignInResult()
        data object Success : UserSignInResult()
    }

    sealed class Role{
        data object Admin: Role()
        data object Supervisor: Role()
        data object Technician: Role()
        data object ServiceAdvisor: Role()
    }


}