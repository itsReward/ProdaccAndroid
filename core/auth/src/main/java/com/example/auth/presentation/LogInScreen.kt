package com.example.auth.presentation

import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.White


@Composable
fun LogInScreen(
    viewModel: LogInViewModel = LogInViewModel(),
){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(Color.Transparent)

    ){
        Text(text = "Welcome", fontWeight = FontWeight.SemiBold, fontSize = 30.sp)

        OutlinedTextField(value = username, onValueChange = { username = it }, shape = RoundedCornerShape(50.dp))
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, shape = RoundedCornerShape(50.dp))
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueA700,
                contentColor = White
            ),
            enabled = true,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "LogIn")
        }
    }
}