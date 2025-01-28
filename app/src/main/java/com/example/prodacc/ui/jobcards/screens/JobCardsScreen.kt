package com.example.prodacc.ui.jobcards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import com.example.designsystem.designComponents.TopBar
import com.example.designsystem.theme.BlueA700
import com.example.prodacc.navigation.NavigationBar
import com.example.prodacc.navigation.Route
import com.example.prodacc.ui.WebSocketStateIndicator
import com.example.prodacc.ui.jobcards.viewModels.JobCardViewModel
import com.example.prodacc.ui.jobcards.viewModels.LoadingState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.TokenManager

@Composable
fun JobCardsScreen(
    navController: NavController,
    viewModel: JobCardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scroll = rememberScrollState()

    val jobCards = viewModel.filteredJobCards.collectAsState()
    val reportsMap = viewModel.reportsMap.collectAsState().value
    val statusMap = viewModel.statusMap.collectAsState().value

    val isRefreshing = viewModel.refreshing.collectAsState().value
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)


    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()){
                WebSocketStateIndicator()
                TopBar(
                    title = "Job Cards",
                    onSearchClick = { navController.navigate( Route.Search.path.replace("{title}", "Job Cards")) },
                    logOut = {
                        TokenManager.saveToken(null)
                        navController.navigate(Route.LogIn.path)
                    }
                )
            }

        }
        , bottomBar = { NavigationBar(navController) }, floatingActionButton = {

            when(SignedInUser.role){
                SignedInUser.Role.Supervisor -> {}
                SignedInUser.Role.Technician -> {}
                else -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(
                                Route.NewJobCard.path
                            )
                        }, shape = CircleShape, containerColor = Color.White
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add JobCard")
                    }
                }
            }


        },
        containerColor = Color.White
    ) { innerPadding ->

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshJobCards() },
            indicatorPadding = PaddingValues(80.dp)
        ) {

            viewModel.jobCardLoadState.collectAsState().value.let { state ->
                when (state) {
                    is LoadingState.Idle -> {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
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
                                Text(text = "Weak Signal, Refresh")
                                Button(onClick = { viewModel.refreshJobCards() }) {
                                    Text(text = "Refresh")
                                }
                            }
                        }
                    }

                    is LoadingState.Loading -> {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
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
                                    color = BlueA700, trackColor = Color.Transparent
                                )
                                Text(text = "Loading JobCards...")
                            }
                        }
                    }

                    is LoadingState.Success -> {

                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .verticalScroll(scroll)
                        ) {


                            JobStatusFilters(
                                onClickAll = { viewModel.onToggleAllFilterChip() },
                                onClickOpen = { viewModel.onToggleOpenFilterChip() },
                                onClickDiagnostics = { viewModel.onToggleDiagnosticsFilterChip() },
                                onClickWorkInProgress = { viewModel.onToggleWorkInProgressChip() },
                                onClickApproval = { viewModel.onToggleApprovalChip() },
                                onClickDone = { viewModel.onToggleDoneChip() },
                                onClickFrozen = { viewModel.onToggleFrozenChip() },
                                onClickTesting = { viewModel.onToggleTesting() },
                                onClickWaitingForPayment = { viewModel.onToggleWaitingForPayment() },
                                jobCardFilterState = viewModel.jobCardsFilter.collectAsState().value
                            )



                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sizeIn(maxHeight = 2000.dp)
                                    .padding(horizontal = 20.dp)
                            ) {

                                if (jobCards.value.isNotEmpty()) {
                                    items(jobCards.value) { jobCard ->
                                        if (jobCards.value.any { it.id == jobCard.id }) {
                                            viewModel.fetchJobCardReports(jobCard.id)
                                            viewModel.fetchJobCardStatus(jobCard.id)
                                        }

                                        if (jobCard == jobCards.value.first()) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }

                                        JobCardDisplayCard(
                                            jobCard = jobCard,
                                            reportsMap = reportsMap,
                                            statusMap = statusMap,
                                            navController = navController
                                        )

                                        if (jobCard == jobCards.value.last()) {
                                            Spacer(modifier = Modifier.height(30.dp))
                                        }
                                    }
                                } else {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 20.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "No JobCards at the moment")
                                        }
                                    }
                                }


                            }

                            when (SignedInUser.role) {
                                is SignedInUser.Role.Technician -> {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                else -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10f))
                                                .clickable {
                                                    navController.navigate(
                                                        Route.Search.path.replace(
                                                            "{title}", "Job Cards"
                                                        )
                                                    )
                                                }
                                                .padding(vertical = 10.dp, horizontal = 20.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.List, "All JobCards")
                                            Text(text = "All JobCards")
                                        }

                                    }
                                }
                            }

                        }

                    }

                    is LoadingState.Error -> {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                                .padding(innerPadding)
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = state.message)
                            Button(onClick = { viewModel.refreshJobCards() }) {
                                Text(text = "Refresh")
                            }
                        }

                    }
                }
            }

        }
    }

}



