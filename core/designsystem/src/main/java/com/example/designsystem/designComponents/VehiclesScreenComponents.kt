package com.example.designsystem.designComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Vehicle
import java.util.UUID
import kotlin.reflect.KFunction1

@Composable
fun VehiclesList(
    regNumber: String,
    vehicleModel : String,
    clientName: String,
    borderColor: Color,
    onClick: () -> Unit
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column (
            modifier = Modifier.fillMaxHeight()
                ,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            LargeTitleText(regNumber)
            BodyText(text = vehicleModel)
        }

        BodyText(text = clientName)
    }
}

@Composable
fun VehicleStatusFilters(
    all : Boolean,
    workshop: Boolean,
    allVehicles: ()->Unit,
    workshopVehicle: ()->Unit
) {


    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, bottom = 5.dp, end = 20.dp)
    ) {

        item {
            FilterChip(
                onClick = allVehicles,
                label = {
                    Text("All")
                },
                selected = all,
                leadingIcon = if (all) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = all,
                    borderColor = Grey
                )
            )
        }

        item {
            FilterChip(
                onClick = workshopVehicle,
                label = {
                    Text("Workshop")
                },
                selected = workshop,
                leadingIcon = if (workshop) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Warehouse,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueA700,
                    containerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White

                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = workshop,
                    borderColor = Grey
                )
            )
        }



    }


}

@Composable
fun ClientsDropDown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    clients: List<Client>,
    onClientSelected: KFunction1<Client, Unit>
){
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(10.dp)
            ,
        properties = PopupProperties(
            excludeFromSystemGesture = false
        )
    ) {

        Row {

            CustomSearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                placeHolder = "Search Clients",
                modifier = Modifier.fillMaxWidth()
            )
        }


        LazyColumn(
            modifier = Modifier.size(width = 800.dp, height = 800.dp)
        ) {
            items(clients){ client ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${client.clientName} ${client.clientSurname}",
                            color = DarkGrey
                        )
                    },
                    onClick = {
                        onClientSelected(client)
                        run(onDismissRequest)
                    },
                    modifier = Modifier.width( 250.dp)
                )

            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
        ) {
            androidx.compose.material3.Button(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }

    }
}

@Composable
fun VehiclesDropDown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    vehicles: List<Vehicle>,
    onVehicleSelected: KFunction1<Vehicle, Unit>,
    newVehicle: () -> Unit = {}
){
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(10.dp)
        ,
        properties = PopupProperties(
            excludeFromSystemGesture = false
        )
    ) {

        Column {

            CustomSearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                placeHolder = "Search Vehicles",
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = newVehicle) {
                    Text(text = "Add New Vehicle")
                }
            }


        }


        LazyColumn(
            modifier = Modifier.size(width = 800.dp, height = 800.dp)
        ) {
            items(vehicles){ vehicle ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${vehicle.color} ${vehicle.model} - ${vehicle.regNumber}",
                            color = DarkGrey
                        )
                    },
                    onClick = {
                        onVehicleSelected(vehicle)
                        run(onDismissRequest)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            androidx.compose.material3.Button(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }

    }
}