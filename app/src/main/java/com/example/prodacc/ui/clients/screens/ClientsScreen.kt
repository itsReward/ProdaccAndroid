package com.example.prodacc.ui.clients.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.designsystem.designComponents.CategorisedList
import com.example.designsystem.designComponents.ListCategory
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.BlueA700
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.clients.viewModels.ClientsViewModel
import com.example.prodacc.ui.vehicles.VehiclesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.TokenManager


@Composable
fun ClientsScreen(
    navController: NavController,
    viewModel: ClientsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val clients = viewModel.clients.collectAsState().value.sortedBy { it.clientName.first() }.groupBy { it.clientName.first() }
        .map { ListCategory(name = it.key.toString(), items = it.value) }

    val refresh = rememberSwipeRefreshState(isRefreshing = viewModel.refreshing.collectAsState().value)

    SwipeRefresh(state = refresh, onRefresh = viewModel::refreshClients){
        Scaffold(
            topBar = {
                TopBar(
                    title = "Clients",
                    onSearchClick = {
                        navController.navigate(
                            Route.Search.path.replace(
                                "{title}",
                                "Clients"
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
                when (SignedInUser.role){
                    SignedInUser.Role.Admin -> {
                        FloatingActionButton(
                            onClick = { navController.navigate(Route.NewClient.path) },
                            shape = CircleShape,
                            containerColor = Color.White
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Client")
                        }
                    }
                    SignedInUser.Role.ServiceAdvisor -> {
                        FloatingActionButton(
                            onClick = { navController.navigate(Route.NewClient.path) },
                            shape = CircleShape,
                            containerColor = Color.White
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Client")
                        }
                    }
                    else -> {

                    }
                }

            }
        ) { innerPadding ->

            when (viewModel.loadState.collectAsState().value){
                is ClientsViewModel.LoadState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
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
                            Text(text = (viewModel.loadState.collectAsState().value as ClientsViewModel.LoadState.Error).message)
                            Button(onClick = { viewModel.refreshClients() }) {
                                Text(text = "Refresh")
                            }
                        }
                    }
                }
                ClientsViewModel.LoadState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
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
                            Text(text = "Could not load clients, try again")
                            Button(onClick = { viewModel.refreshClients() }) {
                                Text(text = "Refresh")
                            }
                        }
                    }
                }
                ClientsViewModel.LoadState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
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
                                color = BlueA700,
                                trackColor = Color.Transparent
                            )
                            Text(text = "Loading Clients...")
                        }
                    }
                }
                ClientsViewModel.LoadState.Success -> {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp)
                    ) {
                        CategorisedList(categories = clients, navController = navController)
                    }
                }
            }



        }
    }



}