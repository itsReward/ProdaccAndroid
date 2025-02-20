package com.example.prodacc.ui.employees.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.contactDetails
import com.example.designsystem.theme.workIcon
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.employees.viewModels.NewEmployeeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEmployeeScreen(
    navController: NavController,
    viewModel: NewEmployeeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val scroll = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("Service Advisor", "Technician", "Supervisor")
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(visible = true, enter = slideInHorizontally()) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()){
                    WebSocketStateIndicator()
                    TopAppBar(
                        title = {
                            LargeTitleText(name = "New Employee")
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }){
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Navigate Back"
                                )
                            }
                        },
                        actions = {
                            Button(
                                onClick = { viewModel.saveEmployee() },
                                modifier = Modifier.clip(RoundedCornerShape(40.dp))
                            ) {
                                Text(text = "Save")
                            }
                        }
                    )
                }

            }
        ) { innerPadding ->

            when (viewModel.loadState.collectAsState().value) {
                is NewEmployeeViewModel.SaveState.Error -> {
                    ErrorStateColumn(
                        title = (viewModel.loadState.collectAsState().value as NewEmployeeViewModel.SaveState.Error).message,
                        buttonOnClick = { viewModel.resetLoadState() },
                        buttonText = "Try Again"
                    )
                }

                is NewEmployeeViewModel.SaveState.Idle -> {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(scroll)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ProfileAvatar(
                                    initials = "New", size = 120.dp, textSize = 40.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {


                            Row {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "",
                                    modifier = Modifier.padding(10.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = state.value.employeeName ?: "",
                                        onValueChange = viewModel::updateFirstName,
                                        label = { Text(text = "First Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )
                                    OutlinedTextField(
                                        value = state.value.employeeSurname ?: "",
                                        onValueChange = viewModel::updateSurname,
                                        label = { Text(text = "Surname") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )


                                }


                            }



                            Row {
                                Icon(
                                    imageVector = contactDetails,
                                    contentDescription = "Contact Details Icon",
                                    modifier = Modifier.padding(10.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = state.value.phoneNumber ?: "",
                                        onValueChange = viewModel::updatePhone,
                                        label = { Text(text = "Phone Number") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )
                                    OutlinedTextField(
                                        value = state.value.homeAddress ?: "",
                                        onValueChange = viewModel::updateAddress,
                                        label = { Text(text = "Home Address") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )


                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = workIcon,
                                    contentDescription = "Work Icon",
                                    modifier = Modifier.padding(10.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = state.value.employeeDepartment ?: "",
                                        onValueChange = viewModel::updateDepartment,
                                        label = { Text(text = "Department") },
                                        readOnly = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        )
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = state.value.employeeRole ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Job Title") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            roles.forEach { role ->
                                                DropdownMenuItem(
                                                    text = { Text(role) },
                                                    onClick = {
                                                        viewModel.updateJobTitle(
                                                            when(role){
                                                                "Service Advisor" -> "serviceAdvisor"
                                                                "Technician" -> "technician"
                                                                "Supervisor" -> "supervisor"
                                                                else -> ""
                                                            }
                                                        )
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                is NewEmployeeViewModel.SaveState.Loading -> {
                    LoadingStateColumn(title = "Saving ...")
                }

                is NewEmployeeViewModel.SaveState.Success -> {
                    LaunchedEffect(Unit) {
                        delay(2000L)
                        navController.navigateUp()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "New Employee Successfully created")
                    }


                }

            }


        }
    }


}