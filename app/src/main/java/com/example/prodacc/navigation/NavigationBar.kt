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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.designsystem.designComponents.ProfileAvatar
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.car
import com.example.designsystem.theme.people
import com.example.designsystem.theme.work
import com.prodacc.data.remote.dao.User

@Composable
fun NavigationBar(
    navController: NavController,
    viewModel: NavigationBarViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser by viewModel.currentUser.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedItem = viewModel.getSelectedItemIndex(currentRoute)
    val items = viewModel.getNavigationItems()

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
                ),
                label = "Animating the Icons"
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
                                MaterialTheme.typography.labelLarge.fontSize
                            } else {
                                MaterialTheme.typography.labelSmall.fontSize
                            }
                        )
                    }
                },
                onClick = {
                    selectedItem = index
                    when (item) {
                        "Job Cards" -> {
                            navController.navigate(Route.JobCards.path) {
                                popUpTo(Route.LogIn.path) { inclusive = true }
                            }
                        }
                        "Vehicles" -> {
                            navController.navigate(Route.Vehicles.path) {
                                popUpTo(Route.JobCards.path)
                            }
                        }
                        "Clients" -> {
                            navController.navigate(Route.Clients.path) {
                                popUpTo(Route.JobCards.path)
                            }
                        }
                        "Employees", "Technicians" -> {
                            navController.navigate(Route.Employees.path) {
                                popUpTo(Route.JobCards.path)
                            }
                        }
                        "Profile" -> {
                            currentUser?.employeeId?.let { employeeId ->
                                navController.navigate(
                                    Route.EmployeeDetails.path.replace(
                                        "{employeeId}",
                                        employeeId.toString()
                                    )
                                )
                            }
                        }
                    }
                },
                icon = {
                    NavigationBarItemIcon(
                        item = item,
                        modifier = Modifier.scale(scale),
                        currentUser = currentUser
                    )
                },
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
fun NavigationBarItemIcon(
    item: String,
    modifier: Modifier,
    currentUser: User?
) {
    when (item) {
        "Job Cards" -> Icon(imageVector = work, contentDescription = "JobCards", modifier = modifier)
        "Vehicles" -> Icon(imageVector = car, contentDescription = "Vehicles", modifier = modifier)
        "Clients" -> Icon(imageVector = people, contentDescription = "Clients", modifier = modifier)
        "Employees", "Technicians" -> Icon(imageVector = people, contentDescription = "Employees", modifier = modifier)
        "Profile" -> {
            currentUser?.let {
                ProfileAvatar(
                    initials = it.employeeName.first().toString().uppercase(),
                    color = Grey,
                    textSize = 18.sp,
                    size = 35.dp
                )
            }
        }
    }
}