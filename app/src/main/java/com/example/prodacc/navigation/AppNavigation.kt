package com.example.prodacc.navigation

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
import com.example.navigation.Route
import com.example.prodacc.ui.jobcards.JobCardDetailScreen
import com.example.prodacc.ui.vehicles.VehicleDetailsScreen
import com.example.prodacc.ui.vehicles.VehiclesScreen


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.LogIn.path) {
        composable(Route.LogIn.path) { LogInScreen(navController) }
        composable(Route.JobCards.path) { JobCardsScreen(navController) }
        composable(Route.Vehicles.path) { VehiclesScreen(navController) }
        composable(Route.Clients.path) { ClientsScreen(navController) }
        composable(Route.Employees.path) { EmployeesScreen(navController) }

        composable(
            route = Route.VehicleDetails.path,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(navController, vehicleId)
        }
        composable(
            route = Route.JobCardDetails.path,
            arguments = listOf(navArgument("jobCardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobCardId = backStackEntry.arguments?.getString("jobCardId") ?: ""
            JobCardDetailScreen(navController, jobCardId)
        }
        // Add more composables for other routes
    }
}
