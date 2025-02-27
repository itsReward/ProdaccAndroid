package com.example.products.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.navigation.NavigationContract
import com.example.products.ui.screens.CategoriesScreen
import com.example.products.ui.screens.HistoryScreen
import com.example.products.ui.screens.NewProductScreen
import com.example.products.ui.screens.ProductScreen
import com.example.products.ui.screens.ProductsScreen
import com.example.products.ui.screens.VehiclesListScreen


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

    composable(
        route = Route.Product.path,
        arguments = listOf(navArgument("productId") { type = NavType.StringType }),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""
        ProductScreen(
            navController = navController, productId = productId
        )
    }

    composable(
        route = Route.NewProduct.path,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(300)
            )
        }
    ) {
        NewProductScreen(
            navController = navController
        )
    }

    composable(
        route = Route.History.path
    ){
        HistoryScreen(navController = navController)
    }
}