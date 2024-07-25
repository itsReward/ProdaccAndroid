package com.example.designsystem.designComponents

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.designsystem.theme.Blue50
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.navigation.Route


@Composable
fun NavigationBar(navController : NavController ){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItem = when (currentRoute) {
        Route.JobCards.path -> 0
        Route.Vehicles.path -> 1
        Route.Clients.path -> 2
        Route.Employees.path -> 3
        else -> 0 // Default to Job Cards
    }


    
    val items = listOf("Job Cards", "Vehicles", "Clients", "Employees")
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.drawWithContent {
            drawContent()
            drawLine(
                color = LightGrey,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 0.5.dp.toPx()
            )
        },
        tonalElevation = 2.dp
    ) {
        items.forEachIndexed { index, item ->

            val scale by animateFloatAsState(
                targetValue = if (selectedItem == index) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                ), label = "Animating the Icons"
            )


            NavigationBarItem(
                selected = selectedItem == index,
                label = {

                        Text(
                            text = item,
                            fontWeight = if (selectedItem == index) {
                                FontWeight.Black
                            } else {
                                FontWeight.SemiBold
                            },
                            fontSize = if (selectedItem == index) {
                                androidx.compose.material3.MaterialTheme.typography.labelLarge.fontSize
                            } else {
                                androidx.compose.material3.MaterialTheme.typography.labelSmall.fontSize
                            }
                        )


                        },
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
                icon = { NavigationBarItemIcon(item = item, Modifier.scale(scale)) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Blue50,
                    unselectedIconColor = Grey,
                    unselectedTextColor = Grey,
                    selectedIconColor = DarkBlue,
                    selectedTextColor = DarkBlue
                )
            )
        }
    }
}

@Composable
fun NavigationBarItemIcon(item: String, modifier: Modifier) {
    return when(item){
        "Job Cards" -> {
            Icon(imageVector = Icons.Default.Work, contentDescription = "JobCards", modifier = modifier )
        }
        "Vehicles" -> {
            Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "Vehicles", modifier = modifier )
        }
        "Clients" -> {
            Icon(imageVector = Icons.Default.People, contentDescription = "Clients", modifier = modifier )
        }
        "Employees" -> {
            Icon(imageVector = Icons.Default.People, contentDescription = "Employees", modifier = modifier )
        }

        else -> {
            //Icon(imageVector = Icons.Rounded.Home, contentDescription = "Home" )
        }
    }
}

