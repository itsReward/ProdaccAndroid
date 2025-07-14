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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.products.viewModels.categories.CategoriesViewModel
import com.example.products.viewModels.categories.CategoriesViewModel.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesWithProducts = viewModel.filteredCategoriesWithProducts.collectAsState().value
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
                            text = "Product Categories",
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
                                Text(text = "Search Categories")
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
                onClick = { viewModel.newCategoryBottomSheetModalVisibilityToggle() },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "NewCategory")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (viewModel.loadingState.collectAsState().value) {
                is LoadingState.Error -> {
                    ErrorComposable(text = (viewModel.loadingState.collectAsState().value as LoadingState.Error).message) {
                        viewModel.refreshData()
                    }
                }

                is LoadingState.Idle -> {}
                is LoadingState.Loading -> {
                    LoadingComposable()
                }

                is LoadingState.Success -> {
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(categoriesWithProducts) { category ->
                            var showProducts by remember { mutableStateOf(false) }

                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardGrey)
                                        .padding(horizontal = 20.dp, vertical = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = category.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (!category.description.isNullOrBlank()) {
                                            Text(
                                                text = category.description!!,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Grey
                                            )
                                        }
                                        Text(
                                            text = "${category.product?.size ?: 0} products",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Grey
                                        )
                                    }

                                    Row {
                                        AnimatedVisibility(visible = showProducts) {
                                            IconButton(onClick = {
                                                viewModel.setSelectedCategory(category.toCategory())
                                                viewModel.onDeleteDialogToggle()
                                            }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    "Delete"
                                                )
                                            }
                                        }
                                        IconButton(onClick = { showProducts = !showProducts }) {
                                            Icon(
                                                if (showProducts) {
                                                    Icons.Default.KeyboardArrowUp
                                                } else {
                                                    Icons.Default.KeyboardArrowDown
                                                },
                                                "Expand"
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = showProducts) {
                                    Column {
                                        val products = category.product ?: emptyList()
                                        if (products.isEmpty()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(20.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "No products in this category",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Grey
                                                )
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.height((products.size * 100).dp)
                                            ) {
                                                items(products) { product ->
                                                    ProductCards(
                                                        product = product,
                                                        onNavigate = {
                                                            navController.navigate("${Route.Product.path}/${product.id}")
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
                }

                else -> {}
            }
        }
    }

    // New Category Bottom Sheet
    if (viewModel.newCategoryBottomSheetModalExpand.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.newCategoryBottomSheetModalVisibilityToggle() }
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Add New Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                CustomTextField(
                    value = viewModel.newCategory.collectAsState().value.name,
                    onValueChange = viewModel::updateCategoryName,
                    isError = false,
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = { Text("Category Name") }
                )

                CustomTextField(
                    value = viewModel.newCategory.collectAsState().value.description ?: "",
                    onValueChange = viewModel::updateCategoryDescription,
                    isError = false,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = { Text("Description (Optional)") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.newCategoryBottomSheetModalVisibilityToggle() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Grey)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { viewModel.saveNewCategory() },
                        modifier = Modifier.weight(1f),
                        enabled = viewModel.newCategory.collectAsState().value.name.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Delete Category Dialog
    if (viewModel.deleteDialogState.collectAsState().value) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteDialogToggle() },
            title = { Text("Delete Category") },
            text = {
                Text("Are you sure you want to delete \"${viewModel.selectedCategory.collectAsState().value?.name}\"?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCategory() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.onDeleteDialogToggle() }) {
                    Text("Cancel")
                }
            }
        )
    }
}