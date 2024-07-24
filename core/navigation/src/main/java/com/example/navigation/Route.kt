package com.example.navigation

sealed class Route(val path: String) {
    object LogIn: Route("login")
    object JobCards: Route("jobcards")
    object Vehicles: Route("vehicles")
    object Clients: Route("clients")
    object Employees: Route("employees")
}