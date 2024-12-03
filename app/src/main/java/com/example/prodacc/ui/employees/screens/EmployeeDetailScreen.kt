package com.example.prodacc.ui.employees.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.DeleteConfirmation
import com.example.designsystem.designComponents.DeleteStateError
import com.example.designsystem.designComponents.DisplayTextField
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.IdleStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.companyIcon
import com.example.designsystem.theme.errorIcon
import com.example.designsystem.theme.workIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModel
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    navController: NavController,
    employeeId: String,
    viewModel: EmployeeDetailsViewModel = viewModel(
        factory = EmployeeDetailsViewModelFactory(employeeId)
    )
) {
    val employee = viewModel.employee.collectAsState().value
    val jobCards = viewModel.jobCards.collectAsState()


    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        }, actions = {
            IconButton(onClick = {
                navController.navigate(
                    Route.EditEmployee.path.replace(
                        "{employeeId}", employee?.id.toString()
                    )
                )
            }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { viewModel.toggleDeleteConfirmation()}) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
            }
        })
    }) { innerPadding ->

        when (viewModel.employeeLoadState.collectAsState().value) {
            is EmployeeDetailsViewModel.EmployeeLoadState.Error -> {
                ErrorStateColumn(
                    title = "Could not load Employee, Try Again",
                    buttonOnClick = viewModel::refreshEmployee,
                    buttonText = "Reload"
                )
            }

            is EmployeeDetailsViewModel.EmployeeLoadState.Idle -> {
                IdleStateColumn(
                    title = "Load Employee",
                    buttonOnClick = viewModel::refreshEmployee,
                    buttonText = "Load"
                )
            }

            is EmployeeDetailsViewModel.EmployeeLoadState.Loading -> {
                LoadingStateColumn(title = "Loading Employee...")
            }

            is EmployeeDetailsViewModel.EmployeeLoadState.Success -> {
                Column(
                    modifier = Modifier.padding(innerPadding)
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
                                initials = "${employee!!.employeeName.first()}${employee.employeeSurname.first()}",
                                size = 120.dp,
                                textSize = 40.sp
                            )
                        }
                        LargeTitleText(name = " ${employee!!.employeeName} ${employee.employeeSurname} ")
                        DisplayTextField(
                            icon = Icons.Outlined.Star,
                            label = "Rating",
                            text = employee.rating.toString(),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardGrey)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.Start
                        ) {


                            DisplayTextField(
                                icon = Icons.Outlined.Phone,
                                label = "Phone",
                                text = employee!!.phoneNumber,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            DisplayTextField(
                                icon = Icons.Outlined.LocationOn,
                                label = "Address",
                                text = employee!!.homeAddress,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )


                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardGrey)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            DisplayTextField(
                                icon = workIcon, label = "Job Title", text = employee!!.employeeRole
                            )

                            DisplayTextField(
                                icon = companyIcon,
                                label = "Department",
                                text = employee.employeeDepartment
                            )

                        }



                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            MediumTitleText(name = "JobCards")

                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(jobCards.value) { jobCards ->
                                if (jobCards != null) {
                                    AllJobCardListItem(jobCardName = jobCards.jobCardName,
                                        closedDate = jobCards.dateAndTimeClosed,
                                        onClick = {
                                            navController.navigate(
                                                Route.JobCardDetails.path.replace(
                                                    "{jobCardId}", jobCards.id.toString()
                                                )
                                            )
                                        })
                                }
                            }
                        }
                    }


                }
            }

            else -> {
            }
        }

        when (viewModel.deleteState.collectAsState().value) {
            is EmployeeDetailsViewModel.DeleteState.Error -> {
                AlertDialog(
                    onDismissRequest = viewModel::resetDeleteState,
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteEmployee() }) {
                            Text(text = "Try Again")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {viewModel.resetDeleteState()}) {
                            Text(text = "Cancel")
                        }
                    },
                    icon = { Icon(
                        imageVector = errorIcon,
                        contentDescription = "Delete Icon",
                        tint = DarkGrey
                    )},
                    title = { Text(text = "Error") },
                    text = {
                        Text(text = (viewModel.deleteState.collectAsState().value as EmployeeDetailsViewModel.DeleteState.Error).message)
                    }
                )
            }
            is EmployeeDetailsViewModel.DeleteState.Idle -> {}
            is EmployeeDetailsViewModel.DeleteState.Loading -> {
                LoadingStateColumn(title = "Deleting ${viewModel.employee.collectAsState().value?.employeeName}")
            }

            is EmployeeDetailsViewModel.DeleteState.Success -> {
                LaunchedEffect(Unit) {
                    delay(1000L)
                    navController.navigateUp()
                }
                Column {
                    Text(text = "Successfully deleted ${viewModel.employee.collectAsState().value?.employeeName} ${viewModel.employee.collectAsState().value?.employeeSurname}")
                }
            }
        }

        when (viewModel.deleteConfirmationState.collectAsState().value) {
            true -> {

                AlertDialog(
                    onDismissRequest = viewModel::closeDeleteConfirmation,
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteEmployee() }) {
                            Text(text = "Delete")
                        }
                                    },
                    dismissButton = {
                        Button(onClick = {viewModel.closeDeleteConfirmation()}) {
                            Text(text = "Cancel")
                        }
                    },
                    icon = { Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Icon",
                        tint = DarkGrey
                    )},
                    title = { Text(text = "Delete Employee?") },
                    text = {
                        Text(text = "Are you sure you want to delete ${viewModel.employee.collectAsState().value?.employeeName} ${viewModel.employee.collectAsState().value?.employeeSurname}")
                    },
                    
                )
            }
            false -> {}
        }
    }
}