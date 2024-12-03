package com.example.prodacc.ui.search.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.ClientListCard
import com.example.designsystem.designComponents.EmployeeListCard
import com.example.designsystem.designComponents.VehiclesList
import com.example.designsystem.theme.LightGrey
import com.example.prodacc.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, title: String) {
    val viewModel = SearchViewModel()

    SearchBar(
        query = viewModel.searchQueryState.value,
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        ),
        onQueryChange = {
            when (title) {
                "Job Cards" -> viewModel.searchJobCards(it)
                "Clients" -> {
                    //viewModel.searchClients(it)
                }
                "Vehicles" -> viewModel.searchVehicles(it)
                "Employees" -> viewModel.searchEmployees(it)
            }

        },
        onSearch = {},
        active = true,
        onActiveChange = {},
        leadingIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        placeholder = { Text(text = "Search $title") }
    ) {

        when (title) {
            "Job Cards" -> LazyColumn {
                items(viewModel.jobCardsList.value) {
                    AllJobCardListItem(it.jobCardName, it.dateAndTimeClosed) {
                        navController.navigate(
                            Route.JobCardDetails.path.replace("{jobCardId}", it.id.toString())
                        )
                    }
                }
            }

            "Clients" -> LazyColumn (
                modifier = Modifier.padding(horizontal = 10.dp)
            ){
                items(viewModel.clientsList.value) {
                    ClientListCard(it) {
                        navController.navigate(
                            Route.ClientDetails.path.replace(
                                "{clientId}",
                                it.id.toString()
                            )
                        )
                    }
                }
            }

            "Vehicles" -> LazyColumn(
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                items(viewModel.vehiclesList.value) {
                    VehiclesList(
                        it.regNumber,
                        it.model,
                        it.clientName,
                        LightGrey
                    ) {
                        navController.navigate(
                            Route.VehicleDetails.path.replace(
                                "{vehicleId}",
                                it.id.toString()
                            )
                        )
                    }
                }
            }

            "Employees" -> LazyColumn {
                items(viewModel.employeesList.value) {
                    EmployeeListCard(it) {
                        navController.navigate(
                            Route.EmployeeDetails.path.replace(
                                "{employeeId}",
                                it.id.toString()
                            )
                        )
                    }
                }
            }
        }
    }
}