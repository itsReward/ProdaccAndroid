package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.EmployeeDropDown
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.VehiclesDropDown
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.car
import com.example.designsystem.theme.person
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.jobcards.viewModels.NewJobCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobCardScreen(
    navController: NavController,
    vehicleId: String
) {

    val viewModel = NewJobCardViewModel(vehicleId = vehicleId)
    var vehicleExpanded by remember { mutableStateOf(false) }
    var serviceAdvisorExpanded by remember { mutableStateOf(false) }
    var supervisorExpanded by remember { mutableStateOf(false) }

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
                        navController.navigateUp()
                    }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                        Text(text = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row {
                Icon(
                    imageVector = car,
                    contentDescription = "Navigate Back",
                    modifier = Modifier.padding(end = 15.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = "${viewModel.vehicleState?.color} ${viewModel.vehicleState?.model} ${viewModel.vehicleState?.regNumber}",
                        onValueChange = {},
                        label = { Text(text = "Vehicle") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { vehicleExpanded = !vehicleExpanded }) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Drop down"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    /*VehiclesDropDown(
                        expanded = vehicleExpanded,
                        onDismissRequest = { vehicleExpanded = !vehicleExpanded },
                        vehicles = viewModel.vehicles,
                        onVehicleSelected = viewModel::updateVehicle,
                        newVehicle = { navController.navigate(route = Route.NewVehicle.path) }
                    )*/

                    OutlinedTextField(
                        value = "${viewModel.vehicleState?.clientName} ${viewModel.vehicleState?.clientSurname}",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(text = "Client") }
                    )

                }


            }





            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(CardGrey)
                    .padding(vertical = 10.dp, horizontal = 5.dp)
            ) {

                // SectionLineHeadingSeperator(heading = "Team")

                TextField(
                    value = "${viewModel.vehicleState?.clientName} ${viewModel.vehicleState?.clientSurname}",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = { Text(text = "Service Advisor") },
                    leadingIcon = { Icon(imageVector = person, contentDescription = "employee") },
                    trailingIcon = {
                        IconButton(onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Drop down"
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                EmployeeDropDown(
                    list = viewModel.employees.collectAsState().value,
                    expanded = viewModel.serviceAdvisorDropDown.value,
                    onDismissRequest = {
                        viewModel.serviceAdvisorDropDown.value =
                            !viewModel.serviceAdvisorDropDown.value
                    },
                    onItemClick = viewModel::updateServiceAdvisor
                )

                TextField(
                    value = "${viewModel.vehicleState?.clientName} ${viewModel.vehicleState?.clientSurname}",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = { Text(text = "Supervisor") },
                    leadingIcon = { Icon(imageVector = person, contentDescription = "employee") },
                    trailingIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Drop down"
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                EmployeeDropDown(
                    list = viewModel.employees.collectAsState().value,
                    expanded = supervisorExpanded,
                    onDismissRequest = { supervisorExpanded = !supervisorExpanded },
                    onItemClick = viewModel::updateSupervisor
                )
            }


        }
    }

}