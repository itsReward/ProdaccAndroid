package com.example.prodacc.ui.login.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.repositories.LogInRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LogInViewModel(
    private val logInRepository: LogInRepository = LogInRepository()
) : ViewModel() {

    private var username = MutableStateFlow("")
    val usernameState = username

    private var password = MutableStateFlow("")
    val passwordState = password

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Idle)
    val loginState = _loginState


    fun onUsernameChange(newUsername: String) {
        username.value = newUsername

    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword

    }

    fun resetLoginState() {
        _loginState.value = LogInState.Idle
    }

    fun logIn(){
        _loginState.value = LogInState.Loading
        viewModelScope.launch {
            logInRepository.login(username.value, password.value).let { result ->
                when (result) {
                    is LogInRepository.LoginResult.Success -> {
                        _loginState.value = LogInState.Success(result.token)
                    }

                    is LogInRepository.LoginResult.Error -> {
                        _loginState.value = LogInState.Error(result.message!!.message)
                    }

                    is LogInRepository.LoginResult.NetworkError -> {
                        _loginState.value = LogInState.Error("Network Error")
                    }

                    else -> {
                        _loginState.value = LogInState.Idle
                    }

                }
            }
        }
    }

}

sealed class LogInState {
    data object Idle: LogInState()
    data object Loading: LogInState()
    data class Success(val token: Any): LogInState()
    data class Error(val message: String): LogInState()
}

