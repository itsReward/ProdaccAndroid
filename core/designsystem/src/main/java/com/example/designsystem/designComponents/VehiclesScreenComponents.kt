package com.example.designsystem.designComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.Vehicle
import kotlin.reflect.KFunction1

@Composable
fun VehiclesList(
    regNumber: String,
    vehicleModel : String,
    clientName: String,
    color: Color,
    onClick: () -> Unit
){
    Row (
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .fillMaxWidth()

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
            Text(text = regNumber)
            Text(
                text = vehicleModel,
                fontStyle = FontStyle.Italic
            )
            //BodyTextItalic(text = vehicleModel)
        }

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            val clientInitials = clientName.trim().split(" ").filter { it.isNotEmpty() }
            // Show client name only if we have initials
            if (clientInitials.isNotEmpty()) {
                BodyText(text = clientInitials[0])
                Spacer(modifier = Modifier.width(10.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))
            // Handle profile avatar display based on available initials
            when {
                clientInitials.size >= 2 -> {
                    // If we have at least 2 parts in the name
                    val firstInitial = clientInitials[0].firstOrNull()?.toString() ?: ""
                    val secondInitial = clientInitials[1].firstOrNull()?.toString() ?: ""
                    ProfileAvatar(
                        initials = "$firstInitial$secondInitial",
                        textSize = 16.sp
                    )
                }
                clientInitials.size == 1 -> {
                    // If we have only one part in the name
                    val initial = clientInitials[0].firstOrNull()?.toString() ?: ""
                    ProfileAvatar(
                        initials = initial,
                        textSize = 16.sp
                    )
                }
                else -> {
                    // Fallback for empty or invalid names
                    ProfileAvatar(
                        initials = "--",
                        textSize = 16.sp,
                        color = Color.LightGray
                    )
                }
            }


        }
        //BodyText(text = clientName)
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
    onClientSelected: KFunction1<Client, Unit>,
    query: String,
    onQueryUpdate: (String) -> Unit
){
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        properties = PopupProperties(
            excludeFromSystemGesture = false,
            focusable = true
        )
    ) {

        TextField(
            value = query,
            onValueChange = onQueryUpdate,
            modifier =  Modifier.fillMaxWidth().focusable(true) // Explicitly make it focusable
                .semantics { // Add semantics for better accessibility
                    contentDescription = "Search vehicles"
                },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "" )
            },
            placeholder = { Text(text = "Search by reg or chassis number")},
            shape = RoundedCornerShape(100),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search, // Add appropriate IME action
                keyboardType = KeyboardType.Text
            ),
            singleLine = true
        )


        clients.forEach { client ->
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
    newVehicle: () -> Unit = {},
    query: String,
    onQueryUpdate: (String) -> Unit
){
    Column {

    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
        ,
        properties = PopupProperties(
            excludeFromSystemGesture = false,
            focusable = true
        ),
    ) {

        Column {
            TextField(
                value = query,
                onValueChange = onQueryUpdate,
                modifier =  Modifier.fillMaxWidth().focusable(true) // Explicitly make it focusable
                    .semantics { // Add semantics for better accessibility
                        contentDescription = "Search vehicles"
                    },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "" )
                },
                placeholder = { Text(text = "Search by reg or chassis number")},
                shape = RoundedCornerShape(100),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search, // Add appropriate IME action
                    keyboardType = KeyboardType.Text
                ),
                singleLine = true
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

        vehicles.forEach{ vehicle ->
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
