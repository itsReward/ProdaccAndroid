package com.example.prodacc.ui.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.vehicleIcon
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.vehicles.viewModels.VehicleDetailsViewModel

@Composable
fun EditVehicleDetailsScreen(
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
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
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

                Button(onClick = { /*TODO*/ }, modifier = Modifier.clip(RoundedCornerShape(40.dp))) {
                    Text(text = "Save", color = Color.White)
                }

                com.example.designsystem.designComponents.IconButton(
                    onClick = { },
                    icon = Icons.Filled.Delete,
                    color = DarkGrey
                )
            }


        }


        //Main Content
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(imageVector = vehicleIcon, contentDescription = "Vehicle Icon", modifier = Modifier.size(60.dp))
                LargeTitleText(name = "${viewModel.vehicle.clientSurname}'s ${viewModel.vehicle.color} ${viewModel.vehicle.model}")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MediumTitleText(name = "Details")
            }






        }
    }
}