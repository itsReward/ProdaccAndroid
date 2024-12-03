package com.example.prodacc.ui.employees.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.contactDetails
import com.example.designsystem.theme.workIcon
import com.example.prodacc.ui.employees.viewModels.EditEmployeeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeScreen(
    navController: NavController,
    employeeId: String
){
    val viewModel: EditEmployeeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditEmployeeViewModel(employeeId = employeeId) as T
            }
        }
    )
    val scroll = rememberScrollState()
    val updateState = viewModel.updateState.collectAsState().value

    AnimatedVisibility(visible = true, enter = slideInHorizontally()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        LargeTitleText(name = "Edit Employee")
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
                            viewModel.updateEmployee()
                        }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                            Text(text = "Save")
                        }
                    }
                )
            }
        ) { innerPadding ->

            when (updateState) {
                is EditEmployeeViewModel.UpdateState.Idle -> {
                    when (viewModel.loadState.collectAsState().value){
                        is EditEmployeeViewModel.LoadState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Text(text = (viewModel.loadState.collectAsState().value as EditEmployeeViewModel.LoadState.Error).message)
                                    Button(onClick = { viewModel.refreshEmployee() }) {
                                        Text(text = "Load")
                                    }
                                }
                            }
                        }
                        is EditEmployeeViewModel.LoadState.Idle -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Text(text = "Load Employee")
                                    Button(onClick = { viewModel.refreshEmployee() }) {
                                        Text(text = "Load")
                                    }
                                }
                            }
                        }
                        is EditEmployeeViewModel.LoadState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = BlueA700,
                                        trackColor = Color.Transparent
                                    )
                                    Text(text = "Loading Employee...")
                                }
                            }
                        }
                        is EditEmployeeViewModel.LoadState.Success -> {
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
                                                value = viewModel.employee.collectAsState().value?.employeeName ?: "",
                                                onValueChange = viewModel::updateFirstName,
                                                label = { Text(text = "First Name") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = viewModel.employee.collectAsState().value?.employeeSurname?: "",
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
                                                value = viewModel.employee.collectAsState().value?.phoneNumber?: "",
                                                onValueChange = viewModel::updatePhone,
                                                label = { Text(text = "Phone Number") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = viewModel.employee.collectAsState().value?.homeAddress?:"",
                                                onValueChange = viewModel::updateAddress,
                                                label = { Text(text = "Home Address") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Row {
                                        Icon(
                                            imageVector = workIcon,
                                            contentDescription = "Work Icon",
                                            modifier = Modifier.padding(10.dp)
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = viewModel.employee.collectAsState().value?.employeeDepartment?: "",
                                                onValueChange = viewModel::updateDepartment,
                                                label = { Text(text = "Department") },
                                                readOnly = false
                                            )
                                            OutlinedTextField(
                                                value = viewModel.employee.collectAsState().value?.employeeRole?: "",
                                                onValueChange = viewModel::updateJobTitle,
                                                label = { Text(text = "Job Title") },
                                                readOnly = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is EditEmployeeViewModel.UpdateState.Loading -> {
                    LoadingStateColumn(title = "Updating...")
                }
                is EditEmployeeViewModel.UpdateState.Success -> {
                    LaunchedEffect(Unit) {
                        delay(3000L)
                        navController.navigateUp()
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "Employee Updated Successfully")
                    }
                }
                is EditEmployeeViewModel.UpdateState.Error -> {
                    ErrorStateColumn(title = updateState.message, buttonOnClick = { navController.navigateUp() }, buttonText = "Go Back")
                }


            }


        }
    }
}