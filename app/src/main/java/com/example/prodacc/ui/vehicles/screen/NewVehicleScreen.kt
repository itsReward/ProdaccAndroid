package com.example.prodacc.ui.vehicles.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ClientsDropDown
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.prodacc.ui.vehicles.viewModels.NewVehicleViewModel

@Composable
fun NewVehicleScreen(
    navController: NavController,
    viewModel: NewVehicleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){
    val uiState = viewModel.uiState.collectAsState().value
    val saveState = viewModel.saveState.collectAsState().value

    var clientExpanded by remember { mutableStateOf(false) }
    var makeExpanded by remember { mutableStateOf(false)}
    var modelExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()


    ) {
        Row(
            modifier = Modifier
                //.background(Color.Blue)
                .wrapContentSize()
                .systemBarsPadding()
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Row(
                modifier = Modifier.weight(2f),
                verticalAlignment = Alignment.CenterVertically
            ) {


                com.example.designsystem.designComponents.IconButton(
                    onClick = { navController.navigateUp() },
                    icon = Icons.Filled.Close,
                    color = DarkGrey
                )
                LargeTitleText(
                    name = "New Vehicle",
                    color = DarkGrey
                )
            }

            Row(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    onClick = {
                        viewModel.saveVehicle()
                    },
                    modifier = Modifier.clip(RoundedCornerShape(40.dp))
                ) {
                    Text(text = "Save", color = Color.White)
                }

            }
        }


        when(saveState){
            is NewVehicleViewModel.SaveState.Error -> {
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
                        Text(text = (viewModel.saveState.collectAsState().value as NewVehicleViewModel.SaveState.Error).message )
                        Button(onClick = { viewModel.resetSaveState() }) {
                            Text(text = "Try Again")
                        }
                    }
                }

            }
            NewVehicleViewModel.SaveState.Idle -> {
                //Main Content
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        LargeTitleText(name = "${uiState.clientSurname}'s ${uiState.model}")
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = viewModel.uiState.value.make?: "Enter Make",
                                    onValueChange = viewModel::updateMake,
                                    label = { Text(text = "Make") },
                                    trailingIcon = {
                                        IconButton(onClick = {makeExpanded = !makeExpanded}) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Drop down"
                                            )
                                        }
                                    },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                DropdownMenu(
                                    expanded = makeExpanded,
                                    onDismissRequest = {makeExpanded = !makeExpanded}
                                ) {
                                    Row {
                                        Text(text = "Vehicle Make", color = DarkGrey)
                                    }
                                    Column {
                                        viewModel.make.forEach{ make ->
                                            DropdownMenuItem(
                                                text = { Text(text = make, color = DarkGrey) },
                                                onClick = {
                                                    viewModel.updateMake(make)
                                                    makeExpanded = !makeExpanded
                                                }
                                            )
                                        }
                                    }

                                }
                            }
                            Row {
                                OutlinedTextField(
                                    value = viewModel.uiState.value.model?: "Enter Model",
                                    onValueChange = viewModel::updateModel,
                                    label = { Text(text = "Model") },
                                    trailingIcon = {
                                        IconButton(onClick = {modelExpanded = !modelExpanded}) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Drop down"
                                            )
                                        }
                                    },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                DropdownMenu(
                                    expanded = modelExpanded,
                                    onDismissRequest = {modelExpanded = !modelExpanded}
                                ) {
                                    Row {
                                        Text(text = "Vehicle Model", color = DarkGrey)
                                    }
                                    Column {
                                        if (uiState.make == "Mercedes-Benz"){
                                            viewModel.vehicleModels["Mercedes-Benz"]?.forEach { model ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = model,
                                                            color = DarkGrey
                                                        )
                                                    },
                                                    onClick = {
                                                        viewModel.updateModel(model)
                                                        modelExpanded = !modelExpanded
                                                    }
                                                )
                                            }
                                        } else if (uiState.make == "Jeep"){
                                            viewModel.vehicleModels["Jeep"]?.forEach { model ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = model,
                                                            color = DarkGrey
                                                        )
                                                    },
                                                    onClick = {
                                                        viewModel.updateMake(model)
                                                        modelExpanded = !modelExpanded
                                                    }
                                                )
                                            }
                                        } else {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "Select Make",
                                                        color = DarkGrey
                                                    )
                                                },
                                                onClick = {

                                                }
                                            )
                                        }

                                    }

                                }
                            }

                            OutlinedTextField(
                                value = viewModel.uiState.value.color?: "",
                                onValueChange = viewModel::updateColor,
                                label = { Text(text = "Color") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {Text("Color")}
                            )
                            OutlinedTextField(
                                value = viewModel.uiState.value.regNumber?: "",
                                onValueChange = viewModel::updateRegNumber,
                                label = { Text(text = "Reg No.") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {Text("Enter Reg Number")}
                            )
                            OutlinedTextField(
                                value = viewModel.uiState.value.chassisNumber?: "",
                                onValueChange = viewModel::updateChassisNumber,
                                label = { Text(text = "Chassis No.") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {Text("Enter Chassis Number")}
                            )
                            Row {
                                OutlinedTextField(
                                    value = "${uiState.clientName} ${uiState.clientSurname}",
                                    onValueChange = {},
                                    label = { Text(text = "Client") },
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = {clientExpanded = !clientExpanded}) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Drop down"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                ClientsDropDown(
                                    expanded = clientExpanded,
                                    onDismissRequest = { clientExpanded = !clientExpanded },
                                    clients = viewModel.clients,
                                    onClientSelected = viewModel::updateClientId
                                )


                            }
                        }
                    }


                }
            }
            is NewVehicleViewModel.SaveState.Success -> {
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
                        Text(text = "Vehicle Saved" )
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Close")
                        }
                    }
                }
            }

            NewVehicleViewModel.SaveState.Loading -> {
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
                        Text(text = "Saving Vehicle...")
                    }
                }
            }
        }

    }
}