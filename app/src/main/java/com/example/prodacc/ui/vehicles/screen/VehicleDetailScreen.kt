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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import com.example.prodacc.ui.vehicles.viewModels.VehicleDetailsViewModel
import com.example.prodacc.ui.vehicles.viewModels.VehicleDetailsViewModelFactory
import com.prodacc.data.SignedInUser

@Composable
fun VehicleDetailsScreen(
    navController: NavController,
    vehicleId: String,
    viewModel: VehicleDetailsViewModel = viewModel(
        factory = VehicleDetailsViewModelFactory(vehicleId)
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

                when(SignedInUser.role){
                    SignedInUser.Role.Supervisor -> {}
                    SignedInUser.Role.Technician -> {}
                    else -> {
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

                        com.example.designsystem.designComponents.IconButton(
                            onClick = {
                                viewModel.toggleDeleteConfirmation()
                            },
                            icon = Icons.Filled.Delete,
                            color = Color.White
                        )
                    }
                }



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
                LoadingStateColumn(title = "Loading Vehicle...")
            }

            is VehicleDetailsViewModel.VehicleLoadState.Success -> {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp, start = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
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
                            .padding(top = 20.dp, start = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        MediumTitleText(name = "Service History")
                    }

                    when (jobCardsLoadState.value) {
                        is VehicleDetailsViewModel.JobCardsLoadState.Error -> {
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

                        is VehicleDetailsViewModel.JobCardsLoadState.Idle -> {
                            IdleStateColumn(
                                title = "Load Vehicle JobCards",
                                buttonOnClick = viewModel::refreshJobCards,
                                buttonText = "Load"
                            )
                        }

                        is VehicleDetailsViewModel.JobCardsLoadState.Loading -> {
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

                        is VehicleDetailsViewModel.JobCardsLoadState.Success -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
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

        when (viewModel.deleteConfirmation.collectAsState().value){
            true -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetDeleteConfirmation() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteVehicle() }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { viewModel.resetDeleteConfirmation() }) {
                            Text(text = "Cancel")
                        }
                    },
                    title = { Text(text = "Delete Confirmation")},
                    text = { Text(text = "Are you sure you want to delete this vehicle?")}
                )
            }
            false -> {

            }
        }

        when(viewModel.deleteState.collectAsState().value){
            is VehicleDetailsViewModel.DeleteVehicleState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetDeleteState() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteVehicle() }) {
                            Text(text = "Try Again")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { viewModel.resetDeleteState() }) {
                            Text(text = "Close")
                        }
                    },
                    title = { Text(text = "Failed to delete")},
                    text = { Text(text = (viewModel.deleteState.collectAsState().value as VehicleDetailsViewModel.DeleteVehicleState.Error).message)}
                )
            }
            is VehicleDetailsViewModel.DeleteVehicleState.Idle -> {}
            is VehicleDetailsViewModel.DeleteVehicleState.Loading -> {
                Dialog(onDismissRequest = { }) {
                    LoadingStateColumn(title = "Deleting Vehicle...")
                }
            }
            is VehicleDetailsViewModel.DeleteVehicleState.Success -> {
                AlertDialog(
                    onDismissRequest = { navController.navigateUp() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteVehicle() }) {
                            Text(text = "Close")
                        }
                    },
                    title = { Text(text = "Vehicle Deleted")}
                )
            }
        }

    }
}