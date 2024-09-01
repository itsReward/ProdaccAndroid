package com.example.prodacc.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prodacc.ui.login.LogInScreen
import com.example.prodacc.ui.clients.ClientsScreen
import com.example.prodacc.ui.employees.EmployeesScreen
import com.example.prodacc.ui.jobcards.JobCardsScreen
import com.example.prodacc.ui.clients.ClientDetailScreen
import com.example.prodacc.ui.clients.EditClientDetailScreen
import com.example.prodacc.ui.clients.NewClientScreen
import com.example.prodacc.ui.employees.EmployeeDetailScreen
import com.example.prodacc.ui.jobcards.JobCardDetailScreen
import com.example.prodacc.ui.vehicles.VehicleDetailsScreen
import com.example.prodacc.ui.vehicles.VehiclesScreen


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.LogIn.path) {
        composable(Route.LogIn.path) { LogInScreen(navController) }
        composable(
            route = Route.JobCards.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1))
            }
        ) { JobCardsScreen(navController) }
        composable(
            route = Route.Vehicles.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1))
            }
        ) { VehiclesScreen(navController) }
        composable(
            route = Route.Clients.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1))
            }
        ) { ClientsScreen(navController) }
        composable(
            route = Route.Employees.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1))
            }
        ) { EmployeesScreen(navController) }

        composable(
            route = Route.VehicleDetails.path,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(navController, vehicleId)
        }
        composable(
            route = Route.JobCardDetails.path,
            arguments = listOf(navArgument("jobCardId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val jobCardId = backStackEntry.arguments?.getString("jobCardId") ?: ""
            JobCardDetailScreen(navController, jobCardId)
        }

        composable(
            route = Route.ClientDetails.path,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            ClientDetailScreen(navController, clientId)
        }

        composable(
            route = Route.EmployeeDetails.path,
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            EmployeeDetailScreen(navController, employeeId)
        }

        composable(
            route = Route.EditClient.path,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            EditClientDetailScreen(navController, clientId)
        }

        composable(
            route = Route.NewClient.path,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { NewClientScreen(navController) }
        // Add more composables for other routes
    }
}
