package com.example.products.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.theme.CardGrey
import com.example.products.navigation.NavigationBar
import com.example.products.ui.components.ErrorComposable
import com.example.products.ui.components.LoadingComposable
import com.example.products.viewModels.vehicles.VehiclesViewModel

@Composable
fun VehiclesListScreen(
    navController: NavController,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles = viewModel.filteredVehicle.collectAsState().value


    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            NavigationBar(navController = navController)
        },
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp, start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Product Vehicles",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row {
                        IconButton(onClick = { viewModel.toggleSearch() }) {
                            Icon(
                                imageVector = if (viewModel.searchExpanded.collectAsState().value) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }

                }

                AnimatedVisibility(visible = viewModel.searchExpanded.collectAsState().value) {

                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        TextField(
                            value = viewModel.searchQuery.collectAsState().value,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(100),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = CardGrey,
                                unfocusedContainerColor = CardGrey
                            ),
                            placeholder = {
                                Text(text = "Search Vehicles")
                            },
                            leadingIcon = {
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    if (viewModel.searchQuery.collectAsState().value == "") {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Search",
                                        )
                                    }

                                }

                            }
                        )
                    }

                }
            }

        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (viewModel.loadingState.collectAsState().value) {
                is VehiclesViewModel.LoadingState.Error -> {
                    ErrorComposable(text = (viewModel.loadingState.collectAsState().value as VehiclesViewModel.LoadingState.Error).message) {
                        viewModel.refreshData()
                    }
                }

                is VehiclesViewModel.LoadingState.Idle -> {}
                is VehiclesViewModel.LoadingState.Loading -> {
                    LoadingComposable()
                }

                is VehiclesViewModel.LoadingState.Success -> {
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(vehicles) { vehicle ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardGrey)
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${vehicle.make} ${vehicle.model} ${vehicle.year}")
                                IconButton(onClick = { }) {
                                    Icon(Icons.Default.KeyboardArrowDown, "Product Drop down")
                                }
                                Text(text = vehicle.toString())
                            }
                        }
                    }
                }
            }

        }

    }
}