package com.example.prodacc.navigation

sealed class Route(val path: String) {
    data object LogIn: Route("login")

    //JobCards Screens
    data object JobCards: Route("jobcards")
    data object JobCardDetails: Route("job_card/{jobCardId}")
    data object EditJobCard: Route("edit_job_card/{jobCardId}")
    data object NewJobCard: Route("new_job_card")


    //Employee Screens
    data object Employees: Route("employees")
    data object EmployeeDetails: Route("employee_details/{employeeId}")
    data object EditEmployee: Route("edit_employee/{employeeId}")
    data object NewEmployee: Route("new_employee")


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

    //Search Screen
    data object Search: Route("search/{title}")

    //Comments Screen
    data object Comments: Route("comments/{jobId}")

}