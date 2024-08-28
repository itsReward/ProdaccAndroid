package com.example.prodacc.ui.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.Details
import com.example.designsystem.designComponents.LargeTitleText
import com.example.designsystem.designComponents.MediumTitleText
import com.example.designsystem.theme.Blue50
import com.example.navigation.Route
import com.example.prodacc.ui.vehicles.viewModels.VehicleDetailsViewModel

@Composable
fun VehicleDetailsScreen(
    navController: NavController,
    vehicleId: String
) {
    val viewModel = VehicleDetailsViewModel(vehicleId = vehicleId)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
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
                    name = "${viewModel.vehicle.clientSurname}'s ${viewModel.vehicle.model}",
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
                        onClick = viewModel.editVehicleViewModel::onEditVehicleToggle,
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
                    .background(Blue50)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {

                Details(
                    edit = viewModel.editVehicleViewModel.editVehicle.value,
                    vehicle = viewModel.editVehicleViewModel.uiState.value,
                    clients = viewModel.clientList,
                    onVehicleModelChange = viewModel.editVehicleViewModel::updateModel,
                    onRegNumberChange = viewModel.editVehicleViewModel::updateRegNumber,
                    onVehicleMakeChange = viewModel.editVehicleViewModel::updateMake,
                    onChassisNumberChange = viewModel.editVehicleViewModel::updateChassisNumber
                ) {

                }


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
                items(viewModel.vehicleJobCards) {
                    AllJobCardListItem(
                        jobCardName = it.jobCardName,
                        closedDate = it.dateAndTimeClosed,
                        onClick = { navController.navigate(Route.JobCardDetails.path.replace("{jobCardId}", it.id.toString())) },
                    )
                }
            }


        }
    }
}