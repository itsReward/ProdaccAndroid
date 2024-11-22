package com.example.prodacc.ui.employees.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.designsystem.designComponents.EmployeeCategoryHeader
import com.example.designsystem.designComponents.EmployeeListCard
import com.example.designsystem.designComponents.TopBar
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeesViewModel
import com.prodacc.data.remote.TokenManager


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeesScreen(navController: NavController) {
    val viewModel = EmployeesViewModel()

    Scaffold(
        topBar = {
            TopBar(
                title = "Employees",
                onSearchClick = {
                    navController.navigate(
                        Route.Search.path.replace(
                            "{title}",
                            "Employees"
                        )
                    )
                },
                logOut = {
                    TokenManager.saveToken(null)
                    navController.navigate(Route.LogIn.path)
                }
            )
        },
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.NewEmployee.path) },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
        ) {
            LazyColumn(
            ) {
                viewModel.employees.forEach { category ->
                    stickyHeader {
                        EmployeeCategoryHeader(text = category.name)
                    }
                    items(category.items) { employee ->
                        EmployeeListCard(employee = employee, onClick = {
                            navController.navigate(
                                Route.EmployeeDetails.path.replace(
                                    "{employeeId}",
                                    employee.id.toString()
                                )
                            )
                        })
                    }
                }
            }
        }


    }
}