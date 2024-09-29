package com.example.prodacc.ui.jobcards.screens

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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.VehiclesDropDown
import com.example.prodacc.ui.jobcards.viewModels.NewJobCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobCardScreen(
    navController: NavController,
) {
    val viewModel = NewJobCardViewModel()
    var vehicleExpanded by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row {
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

                VehiclesDropDown(
                    expanded = vehicleExpanded,
                    onDismissRequest = { vehicleExpanded = !vehicleExpanded },
                    vehicles = viewModel.vehicles,
                    onVehicleSelected = viewModel::updateVehicle
                )


            }

            OutlinedTextField(
                value = "${viewModel.vehicleState?.clientName} ${viewModel.vehicleState?.clientSurname}",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text(text = "Client")}
            )

        }
    }
}