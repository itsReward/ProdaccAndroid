package com.example.prodacc.ui.search.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.AllJobCardListItem
import com.example.designsystem.designComponents.ClientListCard
import com.example.designsystem.designComponents.EmployeeListCard
import com.example.designsystem.designComponents.VehiclesList
import com.example.designsystem.theme.LightCardGrey
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.WebSocketStateIndicator
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    title: String,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.refreshing.collectAsState().value)

    SwipeRefresh(state = swipeRefreshState, onRefresh = viewModel::refreshData) {
        WebSocketStateIndicator(modifier = Modifier.systemBarsPadding())
        SearchBar(
            query = viewModel.searchQueryState.collectAsState().value,
            colors = SearchBarDefaults.colors(
                containerColor = Color.White
            ),
            onQueryChange = {
                viewModel.updateSearchQuery(it)

            },
            onSearch = viewModel::updateSearchQuery,
            active = true,
            onActiveChange = {},
            leadingIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            placeholder = { Text(text = "Search $title") }
        ) {

            when (title) {
                "Job Cards" -> LazyColumn {
                    items(viewModel.filteredJobCards.value) {
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
                    items(viewModel.filteredClients.value) {
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
                    itemsIndexed(viewModel.filteredVehicles.value) {
                        index, it ->
                        VehiclesList(
                            it.regNumber,
                            it.model,
                            it.clientName,
                            if (index%2 == 0) LightCardGrey else Color.White
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
                    items(viewModel.filteredEmployee.value) {
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


}