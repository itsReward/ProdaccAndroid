package com.example.products.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.navigation.NavigationContract
import com.example.products.ui.CategoriesScreen
import com.example.products.ui.ProductsScreen
import com.example.products.ui.VehiclesListScreen


fun NavGraphBuilder.productsGraph(
    onNavigateBack: () -> Unit,
    navController: NavController
) {
    composable(
        route = NavigationContract.Products.ROUTE,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        /*exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }*/
    ) {
        ProductsScreen(
            onNavigateBack = onNavigateBack,
            navController = navController
        )
    }

    composable(
        route = Route.Vehicles.path,
        /*enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }*/
    ) {
        VehiclesListScreen(
            navController = navController
        )
    }

    composable(
        route = Route.Categories.path,
    ) {
        CategoriesScreen(
            navController = navController
        )
    }
}