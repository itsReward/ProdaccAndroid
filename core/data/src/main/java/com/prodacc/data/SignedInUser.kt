package com.prodacc.data

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.User
import com.prodacc.data.repositories.EmployeeRepository
import com.prodacc.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignedInUserManager @Inject constructor(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val dispatchers: CoroutineDispatchers
) {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    private val _role = MutableStateFlow<Role?>(null)
    val role = _role.asStateFlow()

    suspend fun initialize(signedIn: String): UserSignInResult = withContext(dispatchers.io) {
        return@withContext when (val signedInUser = userRepository.getUserByUsername(signedIn)) {
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
                try {
                    _user.value = signedInUser.user
                    _role.value = mapUserRole(signedInUser.user.userRole)
                    fetchEmployee(signedInUser.user.employeeId)
                    UserSignInResult.Success
                } catch (e: Exception) {
                    UserSignInResult.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun mapUserRole(userRole: String): Role {
        return when(userRole) {
            "SERVICE_ADVISOR" -> Role.ServiceAdvisor
            "SUPERVISOR" -> Role.Supervisor
            "TECHNICIAN" -> Role.Technician
            "ADMIN" -> Role.Admin
            else -> Role.Technician
        }
    }

    private suspend fun fetchEmployee(employeeId: UUID) {
        when (val response = employeeRepository.getEmployee(employeeId)) {
            is EmployeeRepository.LoadingResult.EmployeeEntity -> _employee.value = response.employee
            is EmployeeRepository.LoadingResult.Error -> throw Exception(response.message)
            is EmployeeRepository.LoadingResult.NetworkError -> throw Exception("Network Error")
            is EmployeeRepository.LoadingResult.Success -> {} // No-op
        }
    }

    fun clearUser() {
        _user.value = null
        _employee.value = null
        _role.value = null
    }

    sealed class UserSignInResult {
        data class Error(val message: String): UserSignInResult()
        data object Success : UserSignInResult()
    }

    sealed class Role {
        data object Admin: Role()
        data object Supervisor: Role()
        data object Technician: Role()
        data object ServiceAdvisor: Role()
    }
}