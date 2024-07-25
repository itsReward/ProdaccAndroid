package com.example.prodacc.ui.vehicles

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.designsystem.designComponents.NavigationBar
import com.example.designsystem.designComponents.TopBar

@Composable
fun VehiclesScreen(
    navController: NavController
){
    Scaffold (
        topBar = { TopBar("Vehicles") },
        bottomBar = { NavigationBar(navController) }
    ){innerPadding ->
        Text(text = "Vehicles Screen", modifier = Modifier.padding(innerPadding))
    }



}