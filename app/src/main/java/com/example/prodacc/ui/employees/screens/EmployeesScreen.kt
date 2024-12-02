package com.example.prodacc.ui.employees.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.EmployeeCategoryHeader
import com.example.designsystem.designComponents.EmployeeListCard
import com.example.designsystem.designComponents.EmployeeListCategory
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.BlueA700
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.employees.viewModels.EmployeesViewModel
import com.prodacc.data.remote.TokenManager


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeesScreen(
    navController: NavController,
    viewModel: EmployeesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val employees = viewModel.employees.collectAsState().value.sortedBy { it.employeeName.first() }
        .groupBy { it.employeeName.first() }.toSortedMap()
        .map { EmployeeListCategory(name = it.key.toString(), items = it.value) }

    Scaffold(topBar = {
        TopBar(title = "Employees", onSearchClick = {
            navController.navigate(
                Route.Search.path.replace(
                    "{title}", "Employees"
                )
            )
        }, logOut = {
            TokenManager.saveToken(null)
            navController.navigate(Route.LogIn.path)
        })
    }, bottomBar = { NavigationBar(navController) }, floatingActionButton = {
        FloatingActionButton(
            onClick = { navController.navigate(Route.NewEmployee.path) },
            shape = CircleShape,
            containerColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
        }
    }) { innerPadding ->

        when (viewModel.loadState.collectAsState().value) {
            is EmployeesViewModel.LoadState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(text = (viewModel.loadState.collectAsState().value as EmployeesViewModel.LoadState.Error).message)
                        Button(onClick = { viewModel.refreshEmployees() }) {
                            Text(text = "Load")
                        }
                    }
                }
            }

            is EmployeesViewModel.LoadState.Idle -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(text = "Load Employees")
                        Button(onClick = { viewModel.refreshEmployees() }) {
                            Text(text = "Load")
                        }
                    }
                }
            }

            is EmployeesViewModel.LoadState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BlueA700, trackColor = Color.Transparent
                        )
                        Text(text = "Loading Employees...")
                    }
                }
            }

            is EmployeesViewModel.LoadState.Success -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 10.dp)
                ) {
                    LazyColumn(
                    ) {
                        println(employees)
                        employees.forEach { category ->
                            stickyHeader {
                                EmployeeCategoryHeader(text = category.name)
                            }
                            items(category.items) { employee ->
                                EmployeeListCard(
                                    employee = employee,
                                    onClick = {
                                        navController.navigate(
                                            Route.EmployeeDetails.path.replace(
                                                "{employeeId}", employee.id.toString()
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}