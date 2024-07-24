package com.example.employees.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.designsystem.designComponents.navigationBar


@Composable
fun EmployeesScreen(navController : NavController){
    Scaffold(
        bottomBar = { navigationBar(navController) }
    ){
        Text("Employees Screen")
    }
}