package com.example.products.navigation

sealed class Route(val path: String) {
    data object Vehicles: Route("vehiclesList")
    data object Products: Route("products")
    data object Categories: Route("categories")
}