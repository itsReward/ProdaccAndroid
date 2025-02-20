package com.example.prodacc.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.NetworkManager
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.repositories.LogInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val logInRepository: LogInRepository,
    networkManager: NetworkManager,
    private val signedInUserManager: SignedInUserManager,
) : ViewModel() {
    val url = networkManager.localServerIp

    val signedInEmployee = signedInUserManager.employee

    private val _navigateToJobCards = MutableStateFlow(false)
    val navigateToJobCards = _navigateToJobCards.asStateFlow()

    fun onNavigatedToJobCards() {
        _navigateToJobCards.value = false
    }

    private var username = MutableStateFlow("")
    val usernameState = username

    private var password = MutableStateFlow("")
    val passwordState = password

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Idle)
    val loginState = _loginState

    private val _signedInScreen = MutableStateFlow(false)
    val signedInScreen = _signedInScreen.asStateFlow()


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
                        when (val user = signedInUserManager.initialize(username.value)){

                            is SignedInUserManager.UserSignInResult.Error -> LogInState.Error(user.message)
                            is SignedInUserManager.UserSignInResult.Success -> {
                                //WebSocketInstance.reconnectWebSocket()
                                _loginState.value = LogInState.Success(result.token)
                                // Set navigation state instead of directly navigating
                                _navigateToJobCards.value = true
                            }
                        }
                    }

                    is LogInRepository.LoginResult.Error -> {
                        _loginState.value = result.message?.let { LogInState.Error(it.message) }
                            ?: LogInState.Error("Incorrect credentials")
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

