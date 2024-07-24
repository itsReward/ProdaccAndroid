package com.example.vehicles.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.designsystem.designComponents.navigationBar

@Composable
fun VehiclesScreen(
    navController: NavController
){
    Scaffold (
        bottomBar = { navigationBar(navController) }
    ) {
        Text(text = "Vehicles Screen")
    }

}