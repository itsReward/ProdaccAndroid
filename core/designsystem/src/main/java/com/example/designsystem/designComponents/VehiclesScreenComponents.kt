package com.example.designsystem.designComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkOrange
import com.example.designsystem.theme.Green
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGreen
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.LightOrange
import com.example.designsystem.theme.Red

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
            .fillMaxWidth().drawWithContent {
                drawContent()
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column (
            modifier = Modifier.fillMaxHeight()
                ,
            verticalArrangement = Arrangement.Center
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