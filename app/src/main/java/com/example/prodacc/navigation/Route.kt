package com.example.prodacc.navigation

sealed class Route(val path: String) {
    data object LogIn: Route("login")
    data object JobCards: Route("jobcards")
    data object Vehicles: Route("vehicles")
    data object Clients: Route("clients")
    data object Employees: Route("employees")

    data object VehicleDetails: Route("vehicle_details/{vehicleId}")
    data object JobCardDetails: Route("job_card/{jobCardId}")
    data object ClientDetails: Route("client_details/{clientId}")
    data object EmployeeDetails: Route("employee_details/{employeeId}")

    data object EditClient: Route("edit_client/{clientId}")
    data object NewClient: Route("new_client")
}