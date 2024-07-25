package com.example.prodacc.ui.clients

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.designsystem.designComponents.NavigationBar
import com.example.designsystem.designComponents.TopBar


@Composable
fun ClientsScreen( navController : NavController){

    Scaffold(
        topBar = { TopBar("Clients") },
        bottomBar = { NavigationBar(navController) }
    ) {innerPadding ->
        Text(text = "Clients", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }

}