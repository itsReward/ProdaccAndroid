package com.example.prodacc.ui.login

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.PasswordTextField
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.White
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
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
                .background(BlueA700)
                .weight(1.5f)
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 1.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.example.designsystem.R.drawable.jobkeep_logo),
                contentDescription = "logo",
                modifier = Modifier.width(800.dp)
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
                fontSize = 18.sp,
                color = BlueA700,
                modifier = Modifier
                    .padding(start = 13.dp, top = 35.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            TextField(
                value = viewModel.usernameState.collectAsState().value,
                onValueChange = viewModel::onUsernameChange,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(bottom = 5.dp)
                ,
                label = { Text("username") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = LightGrey,
                ),
                singleLine = true,
                isError = when(viewModel.loginState.collectAsState().value ){
                    is LogInState.Error -> true
                    else -> false
                },
            )
            

            PasswordTextField(
                password = viewModel.passwordState.collectAsState().value,
                onPasswordChange = { viewModel.onPasswordChange(it) },
                isError = when(viewModel.loginState.collectAsState().value){
                    is LogInState.Error -> true
                    else -> false
                },

            )

            when (viewModel.loginState.collectAsState().value){
                is LogInState.Error -> {
                    Text(
                        text = (viewModel.loginState.collectAsState().value as LogInState.Error).message,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .padding(start = 5.dp, top = 10.dp, bottom = 25.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    val currentFocus = (context as? Activity)?.currentFocus
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    viewModel.logIn()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueA700, contentColor = White
                ),
                enabled = true,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp)
            ) {
                Text(text = "LogIn")
            }


            Text(
                text = "If you do not have an account, consult the system administrator",
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = DarkGrey,
                modifier = Modifier
                    .padding(start = 5.dp, top = 10.dp, bottom = 25.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //Text(text = ApiInstance.WSURL.collectAsState().value, color = Grey)
            /*TextField(
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
            )*/
            Text(
                textAlign = TextAlign.Center,
                text = "Version 1.0 created with ❤ by Render Creative",
                color = Grey,
                fontSize = 12.sp,
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

                AnimatedVisibility(
                    visible = viewModel.signedInScreen.collectAsState().value,

                ) {
                    Column (
                        modifier = Modifier
                            .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
                            .background(Color.White)
                            .fillMaxSize()
                            .systemBarsPadding(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LargeTitleText(name = "Welcome Back ${SignedInUser.user?.employeeName}")

                    }
                    LaunchedEffect(Unit) {
                        delay(1500L)
                        navController.navigate(Route.JobCards.path)
                    }
                }

            }

            else -> {}
        }
    }
}