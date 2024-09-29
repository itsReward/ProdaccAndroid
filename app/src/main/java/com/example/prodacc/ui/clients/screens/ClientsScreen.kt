package com.example.prodacc.ui.clients.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.CategorisedList
import com.example.designsystem.designComponents.ListCategory
import com.example.prodacc.navigation.NavigationBar
import com.example.designsystem.designComponents.TopBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.clients.viewModels.ClientsViewModel


@Composable
fun ClientsScreen( navController : NavController){

    val viewModel = ClientsViewModel()
    val clients = viewModel.clients.map { ListCategory(name = it.key.toString(), items = it.value) }

    Scaffold(
        topBar = { TopBar("Clients") },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.NewClient.path) },
                shape = CircleShape,
                containerColor = Color.White) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 10.dp)
        ) {
            CategorisedList(categories = clients, navController = navController)
        }

    }

}