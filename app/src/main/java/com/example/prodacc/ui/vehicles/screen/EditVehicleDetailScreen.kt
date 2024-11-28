package com.example.prodacc.ui.vehicles.screen

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.designsystem.theme.DarkGrey
import com.example.prodacc.ui.vehicles.viewModels.EditVehicleDetailsViewModel
import java.util.UUID

@Composable
fun EditVehicleDetailsScreen(
    navController: NavController,
    vehicleId: String
) {
    val viewModel = EditVehicleDetailsViewModel(vehicleId = UUID.fromString(vehicleId))

    var clientExpanded by remember {
        mutableStateOf(false   )
    }
    var makeExpanded by remember {
        mutableStateOf(false)
    }
    var modelExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
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
                    name = "Edit Vehicle",
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
                    onClick = { /*TODO*/ },
                    modifier = Modifier.clip(RoundedCornerShape(40.dp))
                ) {
                    Text(text = "Save", color = Color.White)
                }

                com.example.designsystem.designComponents.IconButton(
                    onClick = { },
                    icon = Icons.Filled.Delete,
                    color = DarkGrey
                )
            }
        }

        if (viewModel.uiState.value != null){
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

                    LargeTitleText(name = "${viewModel.uiState.value!!.clientSurname}'s ${viewModel.uiState.value!!.color} ${viewModel.uiState.value!!.model}")
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
                                value = viewModel.uiState.value!!.make,
                                onValueChange = viewModel::updateMake,
                                label = { Text(text = "Make")},
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
                                value = viewModel.uiState.value!!.model,
                                onValueChange = viewModel::updateModel,
                                label = { Text(text = "Model")},
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
                                    if (viewModel.uiState.value?.make == "Mercedes Benz"){
                                        viewModel.vehicleModels["Mercedes-Benz"]?.forEach{ model ->
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
                                    } else if (viewModel.uiState.value?.make == "Jeep"){
                                        viewModel.vehicleModels["Jeep"]?.forEach{ model ->
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
                                    }

                                }

                            }
                        }

                        OutlinedTextField(
                            value = viewModel.uiState.value!!.color,
                            onValueChange = viewModel::updateColor,
                            label = { Text(text = "Color")},
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = viewModel.uiState.value!!.regNumber,
                            onValueChange = viewModel::updateRegNumber,
                            label = { Text(text = "Reg No.")},
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = viewModel.uiState.value!!.chassisNumber,
                            onValueChange = viewModel::updateChassisNumber,
                            label = { Text(text = "Chassis No.")},
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row {
                            OutlinedTextField(
                                value = "${viewModel.uiState.value!!.clientName} ${viewModel.uiState.value!!.clientSurname}",
                                onValueChange = {},
                                label = { Text(text = "Client")},
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
        } else {
            Text(text = "Loading...")
        }


    }
}