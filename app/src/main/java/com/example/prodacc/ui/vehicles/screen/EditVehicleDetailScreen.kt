package com.example.prodacc.ui.vehicles.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ClientsDropDown
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.IdleStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.theme.DarkGrey
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.vehicles.viewModels.EditVehicleDetailsViewModel
import com.example.prodacc.ui.vehicles.viewModels.EditVehicleDetailsViewModelFactory

@Composable
fun EditVehicleDetailsScreen(
    navController: NavController,
    vehicleId: String,
    viewModel: EditVehicleDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = EditVehicleDetailsViewModelFactory(vehicleId)
    )
) {

    val vehicle by viewModel.vehicle.collectAsState()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()

    ) {
        WebSocketStateIndicator(modifier = Modifier.statusBarsPadding())
        Row(
            modifier = Modifier
                //.background(Color.Blue)
                .wrapContentSize()
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
                    onClick = { viewModel.toggleUpdateConfirmation() },
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


        when (viewModel.loadState.collectAsState().value) {
            is EditVehicleDetailsViewModel.LoadState.Error -> {
                ErrorStateColumn(
                    title = (viewModel.loadState.collectAsState().value as EditVehicleDetailsViewModel.LoadState.Error).message,
                    buttonOnClick = { viewModel.refreshVehicle() },
                    buttonText = "Load"
                )
            }

            is EditVehicleDetailsViewModel.LoadState.Idle -> {
                IdleStateColumn(
                    title = "Load Vehicle",
                    buttonOnClick = { viewModel.refreshVehicle() },
                    buttonText = "Load"
                )
            }

            is EditVehicleDetailsViewModel.LoadState.Loading -> {
                LoadingStateColumn(title = "Loading Vehicle")
            }

            is EditVehicleDetailsViewModel.LoadState.Success -> {
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

                        LargeTitleText(name = "${vehicle!!.clientSurname}'s ${vehicle!!.color} ${vehicle!!.model}")
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
                                    value = vehicle!!.make,
                                    onValueChange = viewModel::updateMake,
                                    label = { Text(text = "Make") },
                                    trailingIcon = {
                                        IconButton(onClick = { viewModel.onVehicleMakeToggle() }) {
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
                                    expanded = viewModel.vehicleMakeDropdown.collectAsState().value,
                                    onDismissRequest = { viewModel.onVehicleMakeToggle() }
                                ) {
                                    Row {
                                        Text(text = "Vehicle Make", color = DarkGrey)
                                    }
                                    Column {
                                        viewModel.make.forEach { make ->
                                            DropdownMenuItem(
                                                text = { Text(text = make, color = DarkGrey) },
                                                onClick = {
                                                    viewModel.updateMake(make)
                                                    viewModel.onVehicleMakeToggle()
                                                }
                                            )
                                        }
                                    }

                                }
                            }
                            Row {
                                OutlinedTextField(
                                    value = vehicle!!.model,
                                    onValueChange = viewModel::updateModel,
                                    label = { Text(text = "Model") },
                                    trailingIcon = {
                                        IconButton(onClick = { viewModel.onVehicleModelToggle() }) {
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
                                    expanded = viewModel.vehicleModelDropdown.collectAsState().value,
                                    onDismissRequest = { viewModel.onVehicleModelToggle() }
                                ) {

                                    Row {
                                        Text(text = "Vehicle Model", color = DarkGrey)
                                    }
                                    Column {
                                        if (vehicle?.make == "Mercedes Benz") {
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
                                                        viewModel.onVehicleModelToggle()
                                                    }
                                                )
                                            }
                                        } else if (vehicle?.make == "Jeep") {
                                            viewModel.vehicleModels["Jeep"]?.forEach { model ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = model,
                                                            color = DarkGrey
                                                        )
                                                    },
                                                    onClick = {
                                                        viewModel.updateModel(model)
                                                        viewModel.onVehicleModelToggle()
                                                    }
                                                )
                                            }
                                        }

                                    }

                                }
                            }

                            OutlinedTextField(
                                value = vehicle!!.color,
                                onValueChange = viewModel::updateColor,
                                label = { Text(text = "Color") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = vehicle!!.regNumber,
                                onValueChange = viewModel::updateRegNumber,
                                label = { Text(text = "Reg No.") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = vehicle!!.chassisNumber,
                                onValueChange = viewModel::updateChassisNumber,
                                label = { Text(text = "Chassis No.") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row {
                                OutlinedTextField(
                                    value = "${vehicle!!.clientName} ${vehicle!!.clientSurname}",
                                    onValueChange = {},
                                    label = { Text(text = "Client") },
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { viewModel.onVehicleClientToggle() }) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowDown,
                                                contentDescription = "Drop down"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                ClientsDropDown(
                                    expanded = viewModel.vehicleClientDropdown.collectAsState().value,
                                    onDismissRequest = { viewModel.onVehicleClientToggle() },
                                    clients = viewModel.filteredClients.collectAsState().value,
                                    onClientSelected = viewModel::updateClientId,
                                    onQueryUpdate = viewModel::onQueryUpdate,
                                    query = viewModel.searchQuery.collectAsState().value
                                )


                            }
                        }
                    }


                }
            }
        }

        when (viewModel.updateConfirmation.collectAsState().value) {
            true -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetUpdateConfirmation() },
                    confirmButton = {
                        Button(onClick = { viewModel.updateVehicleDetails() }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.resetUpdateConfirmation() }) {
                            Text(text = "Cancel")
                        }
                    },
                    title = { Text(text = "Confirm Save") },
                    text = { Text(text = "Are you sure you want to save the changes made to the vehicle") }
                )
            }

            false -> {

            }
        }


        when (viewModel.updateState.collectAsState().value) {
            is EditVehicleDetailsViewModel.UpdateState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetUpdateState() },
                    confirmButton = {
                        Button(onClick = { viewModel.updateVehicleDetails() }) {
                            Text(text = "Try Again")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.resetUpdateState() }) {
                            Text(text = "Cancel")
                        }
                    },
                    title = { Text(text = "Error") },
                    text = { Text(text = (viewModel.updateState.collectAsState().value as EditVehicleDetailsViewModel.UpdateState.Error).message) }
                )
            }

            is EditVehicleDetailsViewModel.UpdateState.Idle -> {

            }

            is EditVehicleDetailsViewModel.UpdateState.Loading -> {
                Dialog(onDismissRequest = {}) {
                    LoadingStateColumn(title = "Updating Vehicle")
                }

            }

            is EditVehicleDetailsViewModel.UpdateState.Success -> {
                AlertDialog(
                    onDismissRequest = { navController.navigateUp() },
                    confirmButton = {
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Close")
                        }
                    },
                    title = { Text(text = "Saved Successful") },
                    text = { Text(text = "Your changes have been saved") }
                )
            }
        }

    }
}
