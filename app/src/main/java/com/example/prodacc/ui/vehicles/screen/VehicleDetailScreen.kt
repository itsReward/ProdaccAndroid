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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.Details
import com.example.designsystem.designComponents.ErrorStateColumn
import com.example.designsystem.designComponents.IdleStateColumn
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.LoadingStateColumn
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeeDetailsViewModel
import com.example.prodacc.ui.vehicles.viewModels.VehicleDetailsViewModel

@Composable
fun VehicleDetailsScreen(
    navController: NavController,
    vehicleId: String,
    viewModel: VehicleDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VehicleDetailsViewModel(vehicleId = vehicleId) as T
            }
        }
    )
) {
    val vehicle = viewModel.vehicle.collectAsState().value
    val loadState = viewModel.loadState.collectAsState()
    val jobCardsLoadState = viewModel.vehicleJobCards.collectAsState()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()

    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 30.dp))
                .background(Color.Blue)
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
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    color = Color.White
                )
                LargeTitleText(
                    name = if (vehicle == null) "Loading ..." else "${vehicle.clientSurname}'s ${vehicle.model}",
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (viewModel.editVehicle.value) {
                    com.example.designsystem.designComponents.IconButton(
                        onClick = viewModel::saveVehicle,
                        icon = Icons.AutoMirrored.Filled.Send,
                        color = Color.White
                    )
                } else {
                    com.example.designsystem.designComponents.IconButton(
                        onClick = {
                            navController.navigate(
                                Route.EditVehicle.path.replace(
                                    "{vehicleId}",
                                    vehicleId
                                )
                            )
                        },
                        icon = Icons.Filled.Edit,
                        color = Color.White
                    )

                }

                com.example.designsystem.designComponents.IconButton(
                    onClick = { },
                    icon = Icons.Filled.Delete,
                    color = Color.White
                )
            }


        }


        //Main Content
        when (loadState.value) {
            is VehicleDetailsViewModel.VehicleLoadState.Idle -> {
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
                        Text(text = "Weak Signal, Refresh")
                        Button(onClick = { viewModel.refreshVehicle() }) {
                            Text(text = "Refresh")
                        }
                    }
                }
            }

            is VehicleDetailsViewModel.VehicleLoadState.Error -> {
                ErrorStateColumn(
                    title = (loadState.value as VehicleDetailsViewModel.VehicleLoadState.Error).message,
                    buttonOnClick = viewModel::refreshVehicle,
                    buttonText = "Refresh"
                )
            }

            is VehicleDetailsViewModel.VehicleLoadState.Loading -> {
                LoadingStateColumn(title = "Loading Vehicles...")
            }

            is VehicleDetailsViewModel.VehicleLoadState.Success -> {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MediumTitleText(name = "Details")
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .wrapContentSize()
                            .fillMaxWidth()
                            .background(CardGrey)
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {

                        Details(
                            vehicle = vehicle!!
                        )


                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MediumTitleText(name = "JobCards")
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        when (jobCardsLoadState.value) {
                            is VehicleDetailsViewModel.JobCardsLoadState.Error -> {
                                item {
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
                                            Text(text = (jobCardsLoadState.value as VehicleDetailsViewModel.JobCardsLoadState.Error).message)
                                            Button(onClick = { viewModel.refreshJobCards() }) {
                                                Text(text = "Refresh")
                                            }
                                        }
                                    }

                                }
                            }

                            is VehicleDetailsViewModel.JobCardsLoadState.Idle -> {

                                item {
                                    IdleStateColumn(
                                        title = "Load Vehicle JobCards",
                                        buttonOnClick = viewModel::refreshJobCards,
                                        buttonText = "Load"
                                    )

                                }
                            }

                            is VehicleDetailsViewModel.JobCardsLoadState.Loading -> {
                                item {
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
                                            Text(text = "Loading JobCards...")
                                        }
                                    }
                                }
                            }

                            is VehicleDetailsViewModel.JobCardsLoadState.Success -> {
                                items((jobCardsLoadState.value as VehicleDetailsViewModel.JobCardsLoadState.Success).jobCards) {
                                    AllJobCardListItem(
                                        jobCardName = it.jobCardName,
                                        closedDate = it.dateAndTimeClosed,
                                        onClick = {
                                            navController.navigate(
                                                Route.JobCardDetails.path.replace(
                                                    "{jobCardId}",
                                                    it.id.toString()
                                                )
                                            )
                                        },
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