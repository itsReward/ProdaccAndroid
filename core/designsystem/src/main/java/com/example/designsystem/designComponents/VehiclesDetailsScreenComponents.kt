package com.example.designsystem.designComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prodacc.data.remote.dao.Vehicle


@Composable
fun Details(
    vehicle: Vehicle
){
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 15.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            DisabledTextField(
                label = "Model",
                text = vehicle.model,
                modifier = Modifier.weight(1f)
            )
            DisabledTextField(
                label = "Make",
                text = vehicle.make,
                modifier = Modifier.weight(1f)
            )
        }


        Row {
            DisabledTextField(
                label = "Color",
                text = vehicle.color,
                modifier = Modifier.weight(1f)
            )
            DisabledTextField(
                label = "Client",
                text = "${vehicle.clientName} ${vehicle.clientSurname}",
                modifier = Modifier.weight(1f)
            )

        }

        Row {
            DisabledTextField(
                label = "Reg.Number",
                text = vehicle.regNumber,
                modifier = Modifier.weight(1f)
            )


        }

        Row {
            DisabledTextField(
                label = "Chassis No.",
                text = vehicle.chassisNumber,
                modifier = Modifier.weight(1f)
            )
        }


    }

}