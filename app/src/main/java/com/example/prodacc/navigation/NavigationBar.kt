package com.example.prodacc.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.car
import com.example.designsystem.theme.people
import com.example.designsystem.theme.person
import com.example.designsystem.theme.work
import com.prodacc.data.SignedInUser


@Composable
fun NavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItem = when (currentRoute) {
        Route.JobCards.path -> 0
        Route.Vehicles.path -> 1
        Route.Clients.path -> 2
        Route.Employees.path -> {
            if (SignedInUser.role == SignedInUser.Role.Supervisor) {
                2
            } else {
                3
            }
        }
        else -> 4 // Default to Job Cards
    }


    val items = when (SignedInUser.role) {
        SignedInUser.Role.Admin -> {
            listOf("Job Cards", "Vehicles", "Clients", "Employees")
        }
        SignedInUser.Role.Technician -> {
            listOf("Job Cards", "Profile")
        }
        SignedInUser.Role.Supervisor -> {
            listOf("Job Cards", "Vehicles", "Technicians", "Profile")
        }
        else -> {
            listOf("Job Cards", "Vehicles", "Clients", "Profile")
        }
    }
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
                    AnimatedVisibility(
                        visible = selectedItem == index,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
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
                    }


                },
                onClick = {
                    selectedItem = index
                    when (item) {
                        "Job Cards" -> {
                            navController.navigate(com.example.prodacc.navigation.Route.JobCards.path) {
                                popUpTo(
                                    com.example.prodacc.navigation.Route.LogIn.path
                                ) { inclusive = true }
                            }
                        }

                        "Vehicles" -> {
                            navController.navigate(Route.Vehicles.path) {
                                popUpTo(
                                    com.example.prodacc.navigation.Route.JobCards.path
                                )
                            }
                        }

                        "Clients" -> {
                            navController.navigate(Route.Clients.path) {
                                popUpTo(
                                    com.example.prodacc.navigation.Route.JobCards.path
                                )
                            }
                        }

                        "Employees" -> {
                            navController.navigate(Route.Employees.path) {
                                popUpTo(
                                    com.example.prodacc.navigation.Route.JobCards.path
                                )
                            }
                        }

                        "Technicians" -> {
                            navController.navigate(Route.Employees.path) {
                                popUpTo(
                                    com.example.prodacc.navigation.Route.JobCards.path
                                )
                            }
                        }

                        "Profile" -> {
                            navController.navigate(
                                Route.EmployeeDetails.path.replace(
                                    "{employeeId}", SignedInUser.user!!.employeeId.toString()
                                )
                            )
                        }
                    }
                },
                icon = { NavigationBarItemIcon(item = item, Modifier.scale(scale)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CardGrey,
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
    return when (item) {
        "Job Cards" -> {
            Icon(imageVector = work, contentDescription = "JobCards", modifier = modifier)
        }

        "Vehicles" -> {
            Icon(imageVector = car, contentDescription = "Vehicles", modifier = modifier)
        }

        "Clients" -> {
            Icon(imageVector = people, contentDescription = "Clients", modifier = modifier)
        }

        "Employees" -> {
            Icon(
                imageVector = people, contentDescription = "Employees", modifier = modifier
            )
        }

        "Technicians" -> {
            Icon(
                imageVector = people, contentDescription = "Employees", modifier = modifier
            )
        }

        "Profile" -> {
            ProfileAvatar(initials = SignedInUser.user!!.employeeName.first().toString().uppercase(), color = Grey, textSize = 18.sp, size = 35.dp)
            //Icon(imageVector = person, contentDescription = "Profile", modifier = Modifier)
        }

        else -> {
            //Icon(imageVector = Icons.Rounded.Home, contentDescription = "Home" )
        }
    }
}

