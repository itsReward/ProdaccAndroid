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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ProductCards
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey
import com.example.products.navigation.NavigationBar
import com.example.products.navigation.Route
import com.example.products.ui.components.CustomTextField
import com.example.products.ui.components.ErrorComposable
import com.example.products.ui.components.LoadingComposable
import com.example.products.viewModels.vehicles.VehiclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesListScreen(
    navController: NavController,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles = viewModel.filteredVehicle.collectAsState().value
    val focusManager = LocalFocusManager.current

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
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.newVehicleBottomSheetModalVisibiltyToggle() },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "NewVehicle")
            }
        }
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

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(vehicles) { vehicle ->
                            var showProducts by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardGrey)
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${vehicle.make} ${vehicle.model} ${vehicle.year}")
                                IconButton(onClick = { showProducts = !showProducts }) {
                                    Icon(
                                        if (showProducts) {
                                            Icons.Default.KeyboardArrowUp
                                        } else {
                                            Icons.Default.KeyboardArrowDown
                                        },

                                        "Product Drop down"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = showProducts) {
                                Column {
                                    if (vehicle.products.isNotEmpty()) {
                                        vehicle.products.forEach {
                                            ProductCards(product = it) {
                                                navController.navigate(
                                                    Route.Product.path.replace(
                                                        "{productId}",
                                                        it.id.toString()
                                                    )
                                                )
                                            }
                                        }

                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 20.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "No Products")
                                        }
                                    }
                                }


                            }
                        }
                    }
                }
            }

        }

        AnimatedVisibility(visible = viewModel.newVehicleBottomSheetModalExpand.collectAsState().value) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.newVehicleBottomSheetModalVisibiltyToggle() },
                containerColor = Color.White
            ) {
                when (viewModel.newVehicleSavingState.collectAsState().value) {
                    is VehiclesViewModel.LoadingState.Error -> {
                        ErrorComposable(text = (viewModel.newVehicleSavingState.collectAsState().value as VehiclesViewModel.LoadingState.Error).message) {
                            viewModel.resetSavingState()
                        }
                    }

                    VehiclesViewModel.LoadingState.Idle -> {

                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "New Product Vehicle",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            CustomTextField(
                                value = viewModel.newVehicle.collectAsState().value.make ?: "",
                                onValueChange = viewModel::onMakeChange,
                                isError = false,
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                ),
                                leadingIcon = {
                                    Text(
                                        text = "Vehicle Make : ",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 10.dp),
                                        color = Grey
                                    )
                                }
                            )

                            CustomTextField(
                                value = viewModel.newVehicle.collectAsState().value.model ?: "",
                                onValueChange = viewModel::onModelChange,
                                isError = false,
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                ),
                                leadingIcon = {
                                    Text(
                                        text = "Vehicle Model : ",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 10.dp),
                                        color = Grey
                                    )
                                }
                            )

                            CustomTextField(
                                value = if (viewModel.newVehicle.collectAsState().value.year == null) "" else viewModel.newVehicle.collectAsState().value.year.toString(),
                                onValueChange = viewModel::onYearChange,
                                isError = false,
                                keyboardActions = KeyboardActions(

                                    onGo = {
                                        viewModel.onSave()
                                    }
                                ),
                                leadingIcon = {
                                    Text(
                                        text = "Year : ",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 10.dp),
                                        color = Grey
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Go
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.onSave() },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = "Save")
                            }


                        }
                    }

                    VehiclesViewModel.LoadingState.Loading -> {
                        LoadingComposable()
                    }

                    VehiclesViewModel.LoadingState.Success -> {

                    }
                }
            }
        }

    }
}