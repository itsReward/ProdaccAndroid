package com.example.prodacc.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prodacc.ui.login.LogInScreen
import com.example.prodacc.ui.clients.ClientsScreen
import com.example.prodacc.ui.employees.screens.EmployeesScreen
import com.example.prodacc.ui.jobcards.JobCardsScreen
import com.example.prodacc.ui.clients.ClientDetailScreen
import com.example.prodacc.ui.clients.EditClientDetailScreen
import com.example.prodacc.ui.clients.NewClientScreen
import com.example.prodacc.ui.employees.screens.EditEmployeeScreen
import com.example.prodacc.ui.employees.screens.EmployeeDetailScreen
import com.example.prodacc.ui.employees.screens.NewEmployeeScreen
import com.example.prodacc.ui.jobcards.JobCardDetailScreen
import com.example.prodacc.ui.vehicles.EditVehicleDetailsScreen
import com.example.prodacc.ui.vehicles.NewVehicleScreen
import com.example.prodacc.ui.vehicles.VehicleDetailsScreen
import com.example.prodacc.ui.vehicles.VehiclesScreen


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.LogIn.path) {
        composable(Route.LogIn.path) { LogInScreen(navController) }

        //JobCardScreens
        composable(
            route = Route.JobCards.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            }
        ) { JobCardsScreen(navController) }
        composable(
            route = Route.JobCardDetails.path,
            arguments = listOf(navArgument("jobCardId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val jobCardId = backStackEntry.arguments?.getString("jobCardId") ?: ""
            JobCardDetailScreen(navController, jobCardId)
        }




        //Vehicles Screen
        composable(
            route = Route.Vehicles.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            }
        ) { VehiclesScreen(navController) }

        composable(
            route = Route.VehicleDetails.path,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(navController, vehicleId)
        }
        composable(
            route = Route.EditVehicle.path,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            EditVehicleDetailsScreen(navController, vehicleId)
        }
        composable(
            route = Route.NewVehicle.path,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { NewVehicleScreen(navController)
        }



        //Clients Screen
        composable(
            route = Route.Clients.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            }
        ) { ClientsScreen(navController) }
        composable(
            route = Route.ClientDetails.path,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            ClientDetailScreen(navController, clientId)
        }
        composable(
            route = Route.EditClient.path,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
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
            }
        ) { NewClientScreen(navController) }



        //Employees Screen
        composable(
            route = Route.Employees.path,
            enterTransition = {
                fadeIn(animationSpec = tween(1))
            }
        ) { EmployeesScreen(navController) }

        composable(
            route = Route.EmployeeDetails.path,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            EmployeeDetailScreen(navController, employeeId)
        }
        composable(
            route = Route.EditEmployee.path,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            EditEmployeeScreen(navController, employeeId)
        }
        composable(
            route = Route.NewEmployee.path,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) {
            NewEmployeeScreen(navController)
        }


        // Add more composables for other routes
    }
}
