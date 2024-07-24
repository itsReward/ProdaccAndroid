package com.example.navigation

sealed class Route(val path: String) {
    data object LogIn: Route("login")
    data object JobCards: Route("jobcards")
    data object Vehicles: Route("vehicles")
    data object Clients: Route("clients")
    data object Employees: Route("employees")
}