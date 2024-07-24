package com.example.designsystem.designComponents

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.navigation.Route


@Composable
fun navigationBar(navController : NavController ){
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Job Cards", "Vehicles", "Clients", "Employees")
    NavigationBar(
        //modifier = Modifier.navigationBarsPadding()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                label = { Text(item) },
                onClick = {
                    selectedItem = index
                    when(item){
                        "Job Cards" -> {
                            navController.navigate(Route.JobCards.path)
                        }
                        "Vehicles" -> {
                            navController.navigate(Route.Vehicles.path)
                        }
                        "Clients" -> {
                            navController.navigate(Route.Clients.path)
                        }
                        "Employees" -> {
                            navController.navigate(Route.Employees.path)
                        }
                    }
                          } ,
                icon = { navigationBarItemIcon(item = item) },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun navigationBarItemIcon(item: String) {
    return when(item){
        "Job Cards" -> {
            Icon(imageVector = Icons.Default.Work, contentDescription = "JobCards" )
        }
        "Vehicles" -> {
            Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "Vehicles" )
        }
        "Clients" -> {
            Icon(imageVector = Icons.Default.People, contentDescription = "Clients" )
        }
        "Employees" -> {
            Icon(imageVector = Icons.Default.People, contentDescription = "Employees" )
        }

        else -> {
            //Icon(imageVector = Icons.Rounded.Home, contentDescription = "Home" )
        }
    }
}

