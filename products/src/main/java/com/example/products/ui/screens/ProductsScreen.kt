package com.example.products.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.designComponents.ProductCards
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.logOutIcon
import com.example.designsystem.theme.pretendard
import com.example.products.navigation.NavigationBar
import com.example.products.navigation.Route
import com.example.products.ui.components.ErrorComposable
import com.example.products.ui.components.LoadingComposable
import com.example.products.viewModels.ProductsViewModel

@Composable
fun ProductsScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {

    val products = viewModel.filteredProducts.collectAsState().value
    val loadState = viewModel.productsLoadState.collectAsState().value

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "jobkeep",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = pretendard,
                                color = BlueA700,
                                fontSize = 30.sp
                            )
                            Text(
                                text = "inventory",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .background(BlueA700)
                                    .padding(horizontal = 6.dp)
                            )
                        }

                    }

                    Row {
                        /*IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Products"
                            )
                        }*/
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = logOutIcon /*Icons.Default.Home*/, contentDescription = "Logout")
                        }
                    }


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
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
                            Text(text = "Search Products")
                        },
                        leadingIcon = {
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(onClick = { viewModel.onSearchQueryChange("") } ) {
                                if (viewModel.searchQuery.collectAsState().value == ""){
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

        },
        bottomBar = {
            NavigationBar(navController = navController)
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.NewProduct.path) },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add product")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when (loadState) {
                    is ProductsViewModel.LoadingState.Error -> {
                        item {
                            ErrorComposable(
                                text = loadState.message,
                                onClick = { viewModel.refreshData() })
                        }
                    }

                    is ProductsViewModel.LoadingState.Idle -> {
                        item {
                            ErrorComposable(
                                text = "Data not loaded",
                                onClick = { viewModel.refreshData() })
                        }
                    }

                    is ProductsViewModel.LoadingState.Loading -> {
                        item {
                            LoadingComposable()
                        }

                    }

                    is ProductsViewModel.LoadingState.Success -> {
                        items(products) { product ->
                            ProductCards(product = product) {
                                navController.navigate(
                                    Route.Product.path.replace(
                                        "{productId}",
                                        product.id.toString()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

