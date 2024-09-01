package com.example.prodacc.navigation

sealed class Route(val path: String) {
    data object LogIn: Route("login")

    //JobCards Screens
    data object JobCards: Route("jobcards")
    data object JobCardDetails: Route("job_card/{jobCardId}")


    //Employee Screens
    data object Employees: Route("employees")
    data object EmployeeDetails: Route("employee_details/{employeeId}")


    //Vehicle Screens
    data object Vehicles: Route("vehicles")
    data object VehicleDetails: Route("vehicle_details/{vehicleId}")
    data object EditVehicle: Route("edit_vehicle/{vehicleId}")
    data object NewVehicle: Route("new_vehicle")


    //Clients Screens
    data object Clients: Route("clients")
    data object ClientDetails: Route("client_details/{clientId}")
    data object EditClient: Route("edit_client/{clientId}")
    data object NewClient: Route("new_client")
}