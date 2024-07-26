package com.example.prodacc.ui.vehicles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.NavigationBar
import com.example.designsystem.designComponents.TopBar

@Composable
fun VehiclesScreen(
    navController: NavController
){
    Scaffold (
        topBar = { TopBar("Vehicles") },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color.White) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ){innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            //Vehicles Screen Content
            Text(text = "Vehicles Screen")
        }

    }



}