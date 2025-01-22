package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.EmployeeDropDown
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.VehiclesDropDown
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.car
import com.example.designsystem.theme.person
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.jobcards.viewModels.NewJobCardViewModel
import com.example.prodacc.ui.jobcards.viewModels.NewJobCardViewModelFactory
import com.prodacc.data.SignedInUser
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobCardScreen(
    navController: NavController,
    viewModel: NewJobCardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = NewJobCardViewModelFactory()
    )
) {

    val vehicle by viewModel.vehicle.collectAsState()
    val supervisor by viewModel.supervisor.collectAsState()
    val serviceAdvisor by viewModel.serviceAdvisor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LargeTitleText(name = "New Job")
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
                        viewModel.saveJob()
                    }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                        Text(text = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val saveState = viewModel.saveState.collectAsState().value) {
            is NewJobCardViewModel.LoadingState.Error -> {
                ErrorStateColumn(
                    title = saveState.message,
                    buttonOnClick = {
                        viewModel.resetSaveState()
                    },
                    buttonText = "Try Again"
                )
            }
            is NewJobCardViewModel.LoadingState.Idle -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Blue50)
                                .padding(horizontal = 10.dp, vertical = 1.dp)
                        ) {
                            Text(text = "Vehicle")
                        }

                    }


                    Column {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = if (vehicle == null) "Select Vehicle" else "${vehicle!!.color} ${vehicle!!.model} ${vehicle!!.regNumber}",
                                onValueChange = {},
                                label = { Text(text = "Vehicle") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {

                                    Icon(
                                        imageVector = car,
                                        contentDescription = "Navigate Back"
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        viewModel.toggleVehiclesDropDown()
                                    }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Drop down"
                                        )
                                    }
                                    VehiclesDropDown(
                                        vehicles = viewModel.vehicles.collectAsState().value,
                                        expanded = viewModel.vehiclesDropdown.value,
                                        onDismissRequest = {
                                            viewModel.toggleVehiclesDropDown()
                                        },
                                        onVehicleSelected = viewModel::updateVehicle,
                                        newVehicle = { navController.navigate(Route.NewVehicle.path) }
                                    )
                                }
                            )


                            OutlinedTextField(
                                value = if (vehicle == null) "Select Vehicle" else "${vehicle?.clientName} ${vehicle?.clientSurname}",
                                onValueChange = {},
                                leadingIcon = {
                                    Icon(imageVector = person, contentDescription = "Client")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                label = { Text(text = "Client") }
                            )

                        }


                    }



                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Blue50)
                                .padding(horizontal = 10.dp, vertical = 1.dp)
                        ) {
                            Text(text = "Team")
                        }

                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                    ) {


                        TextField(
                            value = if (serviceAdvisor == null) "Select Service Advisor" else "${serviceAdvisor!!.employeeName} ${serviceAdvisor!!.employeeSurname}",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            label = { Text(text = "Service Advisor") },
                            leadingIcon = { Icon(imageVector = person, contentDescription = "employee") },
                            trailingIcon = {
                                when(SignedInUser.role){
                                    SignedInUser.Role.ServiceAdvisor -> {}
                                    else -> {
                                        IconButton(onClick = {
                                            viewModel.toggleServiceAdvisor()
                                        }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Drop down"
                                            )
                                        }
                                        EmployeeDropDown(
                                            list = viewModel.serviceAdvisors.collectAsState().value,
                                            expanded = viewModel.serviceAdvisorDropdown.collectAsState().value,
                                            onDismissRequest = {
                                                viewModel.toggleServiceAdvisor()
                                            },
                                            onItemClick = viewModel::updateServiceAdvisor
                                        )
                                    }
                                }

                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardGrey,
                                unfocusedContainerColor = CardGrey,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            )
                        )



                        TextField(
                            value = if (supervisor == null) "Select Supervisor" else "${supervisor!!.employeeName} ${supervisor!!.employeeSurname}",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            label = { Text(text = "Supervisor") },
                            leadingIcon = { Icon(imageVector = person, contentDescription = "employee") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.toggleSupervisorDropdown()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Drop down"
                                    )
                                }

                                EmployeeDropDown(
                                    list = viewModel.supervisors.collectAsState().value,
                                    expanded = viewModel.supervisorDropdown.collectAsState().value,
                                    onDismissRequest = { viewModel.toggleSupervisorDropdown() },
                                    onItemClick = viewModel::updateSupervisor
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardGrey,
                                unfocusedContainerColor = CardGrey,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            )
                        )


                    }


                }
            }
            is NewJobCardViewModel.LoadingState.Loading -> {
                LoadingStateColumn(title = "Creating JobCard")
            }
            is NewJobCardViewModel.LoadingState.NetworkError -> {
                ErrorStateColumn(
                    title = "Network Error, Make sure you have an active connection",
                    buttonOnClick = {
                        viewModel.saveJob()
                    },
                    buttonText = "Try Again"
                )
            }
            is NewJobCardViewModel.LoadingState.Success -> {
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
                    Text(text = "New JobCard Successfully created")
                }
            }
        }

    }

}