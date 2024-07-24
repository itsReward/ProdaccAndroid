package com.example.clients.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.designsystem.designComponents.navigationBar


@Composable
fun ClientsScreen( navController : NavController){

    Scaffold(
        bottomBar = { navigationBar(navController) }
    ) {innerPadding ->
        Text(text = "Clients", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }
}