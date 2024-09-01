package com.example.prodacc.ui.employees

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.EmployeeCategorisedList
import com.example.prodacc.navigation.NavigationBar
import com.example.designsystem.designComponents.TopBar


@Composable
fun EmployeesScreen(navController : NavController){
    val viewModel = EmployeesViewModel()

    Scaffold(
        topBar = { TopBar("Employees") },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color.White) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ){innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).padding(horizontal = 10.dp)) {
            EmployeeCategorisedList(categories = viewModel.employees)
        }



    }
}