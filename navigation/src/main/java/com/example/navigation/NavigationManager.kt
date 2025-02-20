package com.example.navigation

import androidx.navigation.NavController

class NavigationManager( private val navController: NavController) {
    fun navigateToProducts() {
        navController.navigate(NavigationContract.Products.ROUTE)
    }

    // Add other navigation methods as needed
}