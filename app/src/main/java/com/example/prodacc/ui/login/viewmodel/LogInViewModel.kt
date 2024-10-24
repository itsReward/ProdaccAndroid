package com.example.prodacc.ui.login.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.repositories.LogInRepository
import kotlinx.coroutines.launch


class LogInViewModel(
    private val logInRepository: LogInRepository = LogInRepository()
) : ViewModel() {

    private var username = mutableStateOf("")
    val usernameState: State<String> = username
    private var password = mutableStateOf("")
    val passwordState: State<String> = password

    private var color = mutableStateOf(Color.Blue)
    val colorState: State<Color> = color

    fun onUsernameChange(newUsername: String) {
        viewModelScope.launch {
            username.value = newUsername
        }


    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword

    }

    fun onColorChange(newColor: Color = Color.Red) {
        color.value = newColor
    }

}