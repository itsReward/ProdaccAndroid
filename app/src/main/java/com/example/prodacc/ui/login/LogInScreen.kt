package com.example.prodacc.ui.login

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.PasswordTextField
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.White
import com.example.designsystem.theme.errorIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.login.viewmodel.LogInState
import com.example.prodacc.ui.login.viewmodel.LogInViewModel
import com.prodacc.data.SignedInUser
import kotlinx.coroutines.delay


@Composable
fun LogInScreen(
    navController: NavController,
    viewModel: LogInViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
                .background(Color.Blue)
                .weight(1.5f)
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.example.designsystem.R.drawable.prodacc_logo),
                contentDescription = "logo",
                modifier = Modifier.width(200.dp)
            )

        }

        Column(
            modifier = Modifier
                .weight(2.5f)
                .padding(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = "Welcome, Log in",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = BlueA700,
                modifier = Modifier.padding(start = 5.dp, top = 35.dp, bottom = 25.dp)
            )

            TextField(
                value = viewModel.usernameState.collectAsState().value,
                onValueChange = viewModel::onUsernameChange,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                label = { Text("username") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )
            

            PasswordTextField(password = viewModel.passwordState.collectAsState().value) { viewModel.onPasswordChange(it) }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.logIn()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueA700, contentColor = White
                ),
                enabled = true,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "LogIn")
            }


            Text(
                text = "If you do not have an account, consult the system administrator",
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                color = DarkGrey,
                modifier = Modifier.padding(start = 5.dp, top = 25.dp, bottom = 25.dp),
                textAlign = TextAlign.Center
            )


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = viewModel.APIAddress.collectAsState().value,
                onValueChange = viewModel::onAPIAddressChange,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                label = { Text("API Address") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.saveAPIAddress() }
                    ){
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "save"
                        )
                    }
                },
            )
            Text(
                textAlign = TextAlign.Center,
                text = "Version 1.0 created with ❤ by Render Creative",
                color = Grey,
            )
        }

    }

    viewModel.loginState.collectAsState().value.let { state ->
        when (state) {
            is LogInState.Idle -> {
            }
            is LogInState.Loading -> {
                Dialog(onDismissRequest = {}) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .width(240.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BlueA700,
                            trackColor = Color.Transparent
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Logging In...")
                    }

                }
            }
            is LogInState.Success -> {

                AnimatedVisibility(visible = viewModel.signedInScreen.collectAsState().value) {
                    Column (
                        modifier = Modifier
                            .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
                            .background(Color.White)
                            .fillMaxSize()
                            .systemBarsPadding(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LargeTitleText(name = "Welcome")
                        MediumTitleText(name = SignedInUser.user?.employeeName?: "Loading")

                    }
                    LaunchedEffect(Unit) {
                        delay(1500L)
                        navController.navigate(Route.JobCards.path)
                    }
                }

            }
            is LogInState.Error -> {
                Dialog(onDismissRequest = viewModel::resetLoginState) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(imageVector = errorIcon, contentDescription = "error", tint = Color.Red)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = state.message)
                        }

                    }


                }
            }

            else -> {}
        }
    }
}