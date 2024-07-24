package com.example.prodacc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.presentation.LogInScreen
import com.example.clients.presentation.ClientsScreen
import com.example.employees.presentation.EmployeesScreen
import com.example.jobcards.presentation.JobCardsScreen
import com.example.navigation.Route
import com.example.vehicles.presentation.VehiclesScreen


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.LogIn.path) {
        composable(Route.LogIn.path) { LogInScreen(navController) }
        composable(Route.JobCards.path) { JobCardsScreen(navController) }
        composable(Route.Vehicles.path) { VehiclesScreen(navController) }
        composable(Route.Clients.path) { ClientsScreen(navController) }
        composable(Route.Employees.path) { EmployeesScreen(navController) }
        // Add more composables for other routes
    }
}