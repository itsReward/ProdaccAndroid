package com.example.prodacc.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.repositories.LogInRepository
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

    private val _signedInScreen = MutableStateFlow(false)
    val signedInScreen = _signedInScreen.asStateFlow()

    private val _APIAddress = MutableStateFlow(ApiInstance.BASE_URL)
    val APIAddress = _APIAddress

    fun onAPIAddressChange(newAPIAddress: String) {
        _APIAddress.value = newAPIAddress
    }

    fun saveAPIAddress() {
        ApiInstance.BASE_URL = _APIAddress.value
        logInRepository.reinitializeService()
    }

    fun onUsernameChange(newUsername: String) {
        username.value = newUsername

    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword

    }

    private fun signedInScreenShow(){
        _signedInScreen.value = true
    }

    fun resetLoginState() {
        _loginState.value = LogInState.Idle
    }

    fun logIn() {
        _loginState.value = LogInState.Loading
        viewModelScope.launch {
            logInRepository.login(username.value, password.value).let { result ->
                when (result) {
                    is LogInRepository.LoginResult.Success -> {
                        when (val user = SignedInUser.initialize(username.value)){

                            is SignedInUser.UserSignInResult.Error -> LogInState.Error(user.message)
                            is SignedInUser.UserSignInResult.Success -> {
                                signedInScreenShow()
                                println(SignedInUser.user)
                            }
                        }
                        _loginState.value = LogInState.Success(result.token)
                    }

                    is LogInRepository.LoginResult.Error -> {
                        _loginState.value = result.message?.let { LogInState.Error(it.message) }
                            ?: LogInState.Error("Unknown Error")
                    }

                    is LogInRepository.LoginResult.ErrorSingleMessage -> {
                        _loginState.value = LogInState.Error(result.message)
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
    data object Idle : LogInState()
    data object Loading : LogInState()
    data class Success(val token: Any) : LogInState()
    data class Error(val message: String = "Unknown Error") : LogInState()
}

