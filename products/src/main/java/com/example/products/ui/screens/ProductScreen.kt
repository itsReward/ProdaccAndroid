package com.example.products.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.designsystem.theme.BlueA700
import com.example.designsystem.theme.CardGrey
import com.example.designsystem.theme.DarkGreen
import com.example.designsystem.theme.DarkGrey
import com.example.designsystem.theme.Grey
import com.example.designsystem.theme.label
import com.example.designsystem.theme.vehicleIcon
import com.example.products.ui.components.ErrorComposable
import com.example.products.ui.components.LoadingComposable
import com.example.products.ui.components.VehiclesFilterChips
import com.example.products.viewModels.ViewProductViewModel
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductScreen(
    viewModel: ViewProductViewModel = hiltViewModel(),
    productId: String,
    navController: NavController
) {


    var dropDownMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val productVehicles = viewModel.product.collectAsState().value?.vehicles ?: emptyList()

    Box(modifier = Modifier
        .imePadding()
        .navigationBarsPadding()){
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Arrow Back"
                            )
                        }
                        Text(
                            text = if (viewModel.product.collectAsState().value == null){
                                "Loading..."
                            } else {
                                viewModel.product.collectAsState().value!!.partName + " :"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = viewModel.product.collectAsState().value?.partNumber ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        IconButton(onClick = { dropDownMenu = !dropDownMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = dropDownMenu,
                            onDismissRequest = { dropDownMenu = !dropDownMenu },
                            modifier = Modifier
                                .background(Color.White)
                                .widthIn(min = 200.dp)
                                .padding(horizontal = 10.dp)
                        ) {
                            DropdownMenuItem(text = { Text(text = "Replenish") }, onClick = { },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Replenish"
                                    )
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(1))
                                    .background(CardGrey)
                            )
                            DropdownMenuItem(text = { Text(text = "Edit Details") }, onClick = { },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                })
                            DropdownMenuItem(
                                text = { Text(text = "Delete Product") },
                                onClick = { },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                when(viewModel.loading.collectAsState().value){
                    is ViewProductViewModel.OperationState.Error -> {
                        ErrorComposable(text = (viewModel.loading.collectAsState().value as ViewProductViewModel.OperationState.Error).message) {
                            viewModel.refetchProduct()
                        }
                    }
                    ViewProductViewModel.OperationState.Loading -> {
                        LoadingComposable()
                    }
                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardGrey)
                                .padding(10.dp)
                        ) {
                            Text(text = "Product Details")
                        }

                        TextField(
                            value = viewModel.product.collectAsState().value?.partName ?: "",
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Part Name : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Words
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "partName",

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.partNumber ?: "",
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Part Number : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Characters
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "partNumber"

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.description ?: "",
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Description : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Default,
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            maxLines = 4,

                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.inStock.toString(),
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "In Stock : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "inStock"

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.healthyNumber.toString(),
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Healthy No. : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "healthyNUmber"

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.arrivalPrice.toString(),
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Arrival Price : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "arrivalPrice"

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.sellingPrice ?: "",
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Selling Price : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            // isError = viewModel.error.collectAsState().value == "sellingPrice"

                        )

                        TextField(
                            value = viewModel.product.collectAsState().value?.storageLocation ?: "",
                            onValueChange = {},
                            leadingIcon = {
                                Text(
                                    text = "Storage Location : ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Grey
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                capitalization = KeyboardCapitalization.Words,
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            singleLine = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.End
                            ),
                            //isError = viewModel.error.collectAsState().value == "storageLocation"

                        )


                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = label, contentDescription = "Categories")
                            Text(text = "Categories : ")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = {  },
                                label = { Text("Electrical") },
                                shape = RoundedCornerShape(100),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueA700,
                                    containerColor = CardGrey,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    disabledContainerColor = DarkGrey,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = false,
                                    selected = false,
                                    disabledBorderColor = Color.Transparent
                                )

                            )

                            FilterChip(
                                selected = false,
                                onClick = { },
                                label = { Text("Add") },
                                shape = RoundedCornerShape(100),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueA700,
                                    containerColor = CardGrey,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    disabledContainerColor = DarkGrey,
                                ),
                                border = FilterChipDefaults.filterChipBorder(enabled = false, selected = false)

                            )
                        }



                        Spacer(modifier = Modifier.height(20.dp))
                        Column {
                            var dropDownState by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = vehicleIcon, contentDescription = "Vehicles")
                                    Text(text = "Vehicles : ")
                                }

                                IconButton(onClick = { dropDownState = !dropDownState }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "add product")
                                }

                            }

                            AnimatedVisibility(visible = productVehicles.isNotEmpty()) {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    productVehicles.forEach {
                                        VehiclesFilterChips(
                                            vehicle = it,
                                            showRemoveButton = false,
                                            onRemoveButtonClick = {}
                                        )

                                    }
                                }

                            }

                            AnimatedVisibility(visible = productVehicles.isEmpty()) {
                                Text(text = "No stock for any specific vehicles")
                            }

                            DropdownMenu(
                                expanded = dropDownState,
                                onDismissRequest = { dropDownState = !dropDownState },
                                modifier = Modifier.background(Color.White)
                            ) {
                                when (viewModel.vehicleLoadingState.collectAsState().value) {
                                    is ViewProductViewModel.OperationState.Error -> Text("Could not load vehicles")
                                    ViewProductViewModel.OperationState.Idle -> {
                                        Text("Could not load vehicles")
                                    }

                                    ViewProductViewModel.OperationState.Loading -> {
                                        LoadingComposable()
                                    }

                                    is ViewProductViewModel.OperationState.Success -> {
                                        Row(
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            TextField(
                                                value = viewModel.searchQuery.collectAsState().value,
                                                onValueChange = viewModel::updateSearchQuery,
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = "Search Vehicles"
                                                    )
                                                },
                                                shape = RoundedCornerShape(100),
                                                colors = TextFieldDefaults.colors(
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    focusedContainerColor = CardGrey,
                                                    unfocusedContainerColor = CardGrey
                                                ),
                                                placeholder = { Text(text = "Search") }
                                            )
                                        }


                                        viewModel.filteredVehicles.collectAsState().value.forEach {
                                            DropdownMenuItem(
                                                text = { Text(text = "${it.make} ${it.model} ${it.year}") },
                                                onClick = {
                                                    viewModel.associateProductWithVehicle(UUID.fromString(productId),it.id)
                                                    dropDownState = !dropDownState
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

            when (viewModel.updateProductState.collectAsState().value) {
                is ViewProductViewModel.OperationState.Error -> {
                    Text(text = "Could Not Load Data")
                }
                is ViewProductViewModel.OperationState.Idle -> {

                }

                is ViewProductViewModel.OperationState.Loading -> {
                    Dialog(onDismissRequest = { }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Saving...")
                        }
                    }
                }

                is ViewProductViewModel.OperationState.Success -> {
                    AlertDialog(
                        onDismissRequest = { navController.navigateUp() },
                        confirmButton = {

                        },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Success",
                                    tint = DarkGreen
                                )
                                Text(
                                    text = "Success",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                        },
                        text = {
                            Text(
                                text = "New Product Successfully Added",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
    }
}