package com.example.prodacc.ui.vehicles.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.designComponents.VehicleStatusFilters
import com.example.designsystem.designComponents.VehiclesList
import com.example.designsystem.theme.LightGrey
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.vehicles.VehiclesViewModel
import com.prodacc.data.remote.TokenManager

@Composable
fun VehiclesScreen(
    navController: NavController
) {
    val viewModel = VehiclesViewModel()

    Scaffold(

        topBar = {
            TopBar(title = "Vehicles",
                onSearchClick = {
                    navController.navigate(
                        Route.Search.path.replace(
                            "{title}",
                            "Vehicles"
                        )
                    )
                },
                logOut = {
                    TokenManager.saveToken(null)
                    navController.navigate(Route.LogIn.path)
                }
            )
        },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Route.NewVehicle.path)
                }, shape = CircleShape, containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
        ) {
            //Vehicles Screen Content
            Row {
                VehicleStatusFilters(
                    viewModel.allVehicles.value,
                    viewModel.workshopVehicles.value,
                    { viewModel.onAllVehiclesClick() },
                    { viewModel.onWorkshopVehiclesClick() }
                )
            }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    if (viewModel.workshopVehicles.value) {
                        viewModel.activeVehicles
                    } else {
                        viewModel.vehiclesList
                    }
                ) { vehicle ->
                    VehiclesList(
                        regNumber = vehicle.regNumber,
                        vehicleModel = vehicle.model,
                        clientName = "${vehicle.clientName} ${vehicle.clientSurname}",
                        borderColor = if (vehicle != viewModel.activeVehicles.last()) LightGrey else Color.Transparent,
                        onClick = {
                            navController.navigate(
                                Route.VehicleDetails.path.replace(
                                    "{vehicleId}",
                                    vehicle.id.toString()
                                )
                            )
                        }
                    )
                }
            }
        }

    }


}