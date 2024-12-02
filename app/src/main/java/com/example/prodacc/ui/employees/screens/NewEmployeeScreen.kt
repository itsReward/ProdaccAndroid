package com.example.prodacc.ui.employees.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.contactDetails
import com.example.designsystem.theme.workIcon
import com.example.prodacc.ui.employees.viewModels.NewEmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEmployeeScreen(
    navController: NavController
){
    val viewModel = NewEmployeeViewModel()
    val state = viewModel.state
    val scroll = rememberScrollState()

    AnimatedVisibility(visible = true, enter = slideInHorizontally()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        LargeTitleText(name = "New Employee")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Navigate Back"
                            )
                        }
                    },
                    actions = {

                        Button(onClick = {
                            viewModel.saveEmployee()
                            navController.navigateUp()
                        }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                            Text(text = "Save")
                        }
                    }
                )
            }
        ) { innerPadding ->

            when (viewModel.loadState.collectAsState().value) {
                is NewEmployeeViewModel.SaveState.Error -> TODO()
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
                                    initials = "New",
                                    size = 120.dp,
                                    textSize = 40.sp
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
                                        value = state.value?.employeeName ?: "",
                                        onValueChange = viewModel::updateFirstName,
                                        label = { Text(text = "First Name") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = state.value?.employeeSurname ?: "",
                                        onValueChange = viewModel::updateSurname,
                                        label = { Text(text = "Surname") },
                                        modifier = Modifier.fillMaxWidth()
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
                                        value = state.value?.phoneNumber ?: "",
                                        onValueChange = viewModel::updatePhone,
                                        label = { Text(text = "Phone Number") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = state.value?.homeAddress ?:"",
                                        onValueChange = viewModel::updateAddress,
                                        label = { Text(text = "Home Address") },
                                        modifier = Modifier.fillMaxWidth()
                                    )


                                }
                            }

                            Row (
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
                                        value = state.value?.employeeDepartment ?: "",
                                        onValueChange = viewModel::updateDepartment,
                                        label = { Text(text = "Department") },
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = state.value?.employeeRole ?: "",
                                        onValueChange = viewModel::updateJobTitle,
                                        label = { Text(text = "Job Title") },
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )


                                }
                            }


                        }
                    }
                }
                is NewEmployeeViewModel.SaveState.Loading -> TODO()
                is NewEmployeeViewModel.SaveState.Success -> TODO()
            }



        }
    }


}