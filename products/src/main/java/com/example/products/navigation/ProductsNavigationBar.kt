package com.example.products.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkBlue
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.LightGrey
import com.example.designsystem.theme.car
import com.example.designsystem.theme.categories
import com.example.designsystem.theme.work

@Composable
fun NavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItem = when (currentRoute) {
        Route.Products.path -> 0
        Route.Vehicles.path -> 1
        Route.Categories.path -> 2
        else -> 0 // Default to Products
    }


    val items = listOf("Products", "Vehicles", "Categories")

    androidx.compose.material3.NavigationBar(
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
                        "Products" -> {
                            navController.navigate(Route.Products.path) {
                                popUpTo(
                                    Route.Products.path
                                ) { inclusive = true }
                            }
                        }

                        "Vehicles" -> {
                            navController.navigate(Route.Vehicles.path) {
                                popUpTo(
                                    Route.Products.path
                                )
                            }
                        }
                        "Categories" -> {
                            navController.navigate(Route.Categories.path) {
                                popUpTo(
                                    Route.Products.path
                                )
                            }
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
        "Products" -> {
            Icon(imageVector = work, contentDescription = "JobCards", modifier = modifier)
        }

        "Vehicles" -> {
            Icon(imageVector = car, contentDescription = "Vehicles", modifier = modifier)
        }

        "Categories" -> {
            Icon(imageVector = categories, contentDescription = "Categories", modifier = Modifier )
        }

        else -> {
            //Icon(imageVector = Icons.Rounded.Home, contentDescription = "Home" )
        }
    }
}
