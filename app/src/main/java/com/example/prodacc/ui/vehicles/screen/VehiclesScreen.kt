package com.example.prodacc.ui.vehicles.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.designComponents.VehicleStatusFilters
import com.example.designsystem.designComponents.VehiclesList
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.LightGrey
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.vehicles.VehiclesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.prodacc.data.remote.TokenManager

@Composable
fun VehiclesScreen(
    navController: NavController,
    viewModel: VehiclesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
)
{
    val vehicles = viewModel.vehicles.collectAsState().value
    val activeVehicles = viewModel.activeVehicles.collectAsState()

    val refresh = rememberSwipeRefreshState(isRefreshing = viewModel.refreshing.collectAsState().value)

    SwipeRefresh(state = refresh, onRefresh = viewModel::refreshVehicles) {
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
                when (viewModel.vehicleLoadState.collectAsState().value) {
                    is VehiclesViewModel.VehicleLoadState.Error -> {
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
                                Text(text = (viewModel.vehicleLoadState.collectAsState().value as VehiclesViewModel.VehicleLoadState.Error).message)
                                Button(onClick = { viewModel.refreshVehicles() }) {
                                    Text(text = "Refresh")
                                }
                            }
                        }
                    }
                    VehiclesViewModel.VehicleLoadState.Idle -> {
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
                                Text(text = "Load Vehicles")
                                Button(onClick = { viewModel.refreshVehicles() }) {
                                    Text(text = "Load")
                                }
                            }
                        }
                    }
                    VehiclesViewModel.VehicleLoadState.Loading -> {
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
                                Text(text = "Loading Vehicles...")
                            }
                        }
                    }
                    VehiclesViewModel.VehicleLoadState.Success -> {
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

                            if (viewModel.workshopVehicles.value){
                                if (activeVehicles.value.isEmpty()){
                                    item {
                                        Text(text = "No active vehicles")
                                    }
                                } else {
                                    items(
                                        activeVehicles.value
                                    ) { vehicle ->
                                        VehiclesList(
                                            regNumber = vehicle.regNumber,
                                            vehicleModel = vehicle.model,
                                            clientName = "${vehicle.clientName} ${vehicle.clientSurname}",
                                            borderColor = if (vehicle != activeVehicles.value.last()) LightGrey else Color.Transparent,
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

                            } else {
                                items(
                                    vehicles
                                ) { vehicle ->
                                    VehiclesList(
                                        regNumber = vehicle.regNumber,
                                        vehicleModel = vehicle.model,
                                        clientName = "${vehicle.clientName} ${vehicle.clientSurname}",
                                        borderColor = if (vehicle != vehicles.last()) LightGrey else Color.Transparent,
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


            }

        }
    }




}