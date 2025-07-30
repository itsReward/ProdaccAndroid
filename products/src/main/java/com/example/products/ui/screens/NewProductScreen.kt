package com.example.products.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.products.viewModels.NewProductViewModel

@Composable
fun NewProductScreen(
    viewModel: NewProductViewModel = hiltViewModel(), navController: NavController
) {

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back Navigation"
                        )
                    }
                    Text(
                        // Assuming your ViewModel exposes productName directly or through a state holder
                        text = viewModel.productNameString.collectAsState().value.ifEmpty { "New Product" },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            modifier = Modifier.systemBarsPadding(),
            bottomBar = {
                Row(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.saveProduct() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Save")
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardGrey)
                        .padding(10.dp)
                ) {
                    Text(text = "Product Details")
                }

                // productCode
                TextField(
                    value = viewModel.productCodeString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onProductCodeChange,
                    label = { Text("Product Code *") },
                    leadingIcon = {
                        Text(
                            text = "Product Code : ",
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
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "productCode" // Assuming error state management
                )

                // productName
                TextField(
                    value = viewModel.productNameString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onProductNameChange,
                    label = { Text("Product Name *") },
                    leadingIcon = {
                        Text(
                            text = "Product Name : ",
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
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "productName"
                )

                // description
                TextField(
                    value = viewModel.descriptionString.collectAsState().value, // Assuming StateFlow<String?>
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Description") },
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
                        imeAction = ImeAction.Next, // Or ImeAction.Default if it's the last text field before non-text inputs
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    maxLines = 4
                    // No isError for optional field, unless specific validation needed
                )

                // categoryId - This might be a Dropdown or a search field depending on UX
                // For simplicity, using a TextField. You might need a more complex component.
                TextField(
                    value = viewModel.categoryIdString.collectAsState().value?.toString() ?: "", // Assuming StateFlow<UUID?>
                    onValueChange = { viewModel.onCategoryIdChange(it) }, // You'll need to handle String to UUID? conversion
                    label = { Text("Category ID") },
                    leadingIcon = {
                        Text(
                            text = "Category ID : ",
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true
                )

                // brand
                TextField(
                    value = viewModel.brandString.collectAsState().value ?: "", // Assuming StateFlow<String?>
                    onValueChange = viewModel::onBrandChange,
                    label = { Text("Brand") },
                    leadingIcon = {
                        Text(
                            text = "Brand : ",
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
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true
                )

                // unitOfMeasure
                TextField(
                    value = viewModel.unitOfMeasureString.collectAsState().value ?: "", // Assuming StateFlow<String?>
                    onValueChange = viewModel::onUnitOfMeasureChange,
                    label = { Text("Unit of Measure") },
                    leadingIcon = {
                        Text(
                            text = "Unit of Measure : ",
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
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true
                )

                // minimumStock
                TextField(
                    value = viewModel.minimumStockString.collectAsState().value, // Assuming StateFlow<String> for input, convert to Int in VM
                    onValueChange = viewModel::onMinimumStockChange,
                    label = { Text("Minimum Stock *") },
                    leadingIcon = {
                        Text(
                            text = "Minimum Stock : ",
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
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "minimumStock"
                )

                // maximumStock
                TextField(
                    value = viewModel.maximumStockString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onMaximumStockChange,
                    label = { Text("Maximum Stock *") },
                    leadingIcon = {
                        Text(
                            text = "Maximum Stock : ",
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
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "maximumStock"
                )

                // costPrice
                TextField(
                    value = viewModel.costPriceString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onCostPriceChange,
                    label = { Text("Cost Price *") },
                    leadingIcon = {
                        Text(
                            text = "Cost Price : ",
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
                        keyboardType = KeyboardType.Number // Or KeyboardType.Decimal if using Material 3 TextFields that support it
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "costPrice"
                )

                // sellingPrice
                TextField(
                    value = viewModel.sellingPriceString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onSellingPriceChange,
                    label = { Text("Selling Price *") },
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
                        keyboardType = KeyboardType.Number // Or KeyboardType.Decimal
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "sellingPrice"
                )

                // markupPercentage
                TextField(
                    value = viewModel.markupPercentageString.collectAsState().value, // Assuming StateFlow<String>
                    onValueChange = viewModel::onMarkupPercentageChange,
                    label = { Text("Markup Percentage *") },
                    leadingIcon = {
                        Text(
                            text = "Markup % : ",
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
                        keyboardType = KeyboardType.Number // Or KeyboardType.Decimal
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    isError = viewModel.errorField.collectAsState().value == "markupPercentage"
                )

                // supplierId - Similar to categoryId, this might be a Dropdown/Search
                TextField(
                    value = viewModel.supplierIdString.collectAsState().value?.toString() ?: "", // Assuming StateFlow<UUID?>
                    onValueChange = { viewModel.onSupplierIdChange(it) }, // Handle String to UUID?
                    label = { Text("Supplier ID") },
                    leadingIcon = {
                        Text(
                            text = "Supplier ID : ",
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // Potentially the last field
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        // Optionally trigger save or validation
                    }),
                    singleLine = true
                )


                // --- Your existing UI for Categories and Vehicles ---
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
                        selected = false, // Replace with state from ViewModel
                        onClick = { /* TODO: viewModel.onCategorySelected("Electrical") */ },
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
                            enabled = false, // This seems odd, usually should be true
                            selected = false,
                            disabledBorderColor = Color.Transparent
                        )
                    )

                    FilterChip(
                        selected = false, // Replace with state from ViewModel
                        onClick = { /* TODO: Show dialog or navigate to add category */ },
                        label = { Text("Add") },
                        shape = RoundedCornerShape(100),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueA700,
                            containerColor = CardGrey,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            disabledContainerColor = DarkGrey,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = false, // This seems odd, usually should be true
                            selected = false
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = vehicleIcon, contentDescription = "Vehicles")
                    Text(text = "Vehicles : ")
                    // Add vehicle selection UI here
                }
                Spacer(modifier = Modifier.height(20.dp)) // Extra space before bottom bar
            }

            // --- Your existing OperationState handling ---
            when (val operationState = viewModel.addProductState.collectAsState().value) {
                is NewProductViewModel.OperationState.Error -> {
                    // Consider showing a Snackbar or inline error message
                    LaunchedEffect(operationState) {
                        // Show a toast or snackbar with operationState.message
                    }
                }
                is NewProductViewModel.OperationState.Idle -> {
                    // Nothing specific to show
                }
                is NewProductViewModel.OperationState.Loading -> {
                    Dialog(onDismissRequest = { /* Prevent dismissal while loading */ }) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Saving...")
                        }
                    }
                }
                is NewProductViewModel.OperationState.Success -> {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.resetOperationState() // Important to reset state
                            navController.navigateUp()
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.resetOperationState()
                                navController.navigateUp()
                            }) {
                                Text("OK")
                            }
                        },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Success",
                                    tint = DarkGreen // Consider using MaterialTheme.colorScheme.primary or secondary
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

// Dummy ViewModel structure for reference - you'll need to implement this
// @HiltViewModel
// class NewProductViewModel @Inject constructor() : ViewModel() {
//     private val _productCode = MutableStateFlow("")
//     val productCode: StateFlow<String> = _productCode.asStateFlow()
//     fun onProductCodeChange(newCode: String) { _productCode.value = newCode }

//     private val _productName = MutableStateFlow("")
//     val productName: StateFlow<String> = _productName.asStateFlow()
//     fun onProductNameChange(newName: String) { _productName.value = newName }

//     private val _description = MutableStateFlow<String?>(null)
//     val description: StateFlow<String?> = _description.asStateFlow()
//     fun onDescriptionChange(newDescription: String?) { _description.value = newDescription }

//     private val _categoryId = MutableStateFlow<UUID?>(null)
//     val categoryId: StateFlow<UUID?> = _categoryId.asStateFlow()
//     fun onCategoryIdChange(idString: String) {
//         _categoryId.value = try { UUID.fromString(idString.trim()) } catch (e: IllegalArgumentException) { null }
//     }

//     private val _brand = MutableStateFlow<String?>(null)
//     val brand: StateFlow<String?> = _brand.asStateFlow()
//     fun onBrandChange(newBrand: String?) { _brand.value = newBrand }

//     private val _unitOfMeasure = MutableStateFlow<String?>(null)
//     val unitOfMeasure: StateFlow<String?> = _unitOfMeasure.asStateFlow()
//     fun onUnitOfMeasureChange(newUnit: String?) { _unitOfMeasure.value = newUnit }

//     private val _minimumStock = MutableStateFlow("") // Keep as String for input
//     val minimumStock: StateFlow<String> = _minimumStock.asStateFlow()
//     fun onMinimumStockChange(newStock: String) { _minimumStock.value = newStock.filter { it.isDigit() } }

//     private val _maximumStock = MutableStateFlow("")
//     val maximumStock: StateFlow<String> = _maximumStock.asStateFlow()
//     fun onMaximumStockChange(newStock: String) { _maximumStock.value = newStock.filter { it.isDigit() } }

//     private val _costPrice = MutableStateFlow("")
//     val costPrice: StateFlow<String> = _costPrice.asStateFlow()
//     fun onCostPriceChange(newPrice: String) { _costPrice.value = newPrice } // Add decimal validation

//     private val _sellingPrice = MutableStateFlow("")
//     val sellingPrice: StateFlow<String> = _sellingPrice.asStateFlow()
//     fun onSellingPriceChange(newPrice: String) { _sellingPrice.value = newPrice } // Add decimal validation

//     private val _markupPercentage = MutableStateFlow("")
//     val markupPercentage: StateFlow<String> = _markupPercentage.asStateFlow()
//     fun onMarkupPercentageChange(newPercentage: String) { _markupPercentage.value = newPercentage } // Add decimal validation

//     private val _supplierId = MutableStateFlow<UUID?>(null)
//     val supplierId: StateFlow<UUID?> = _supplierId.asStateFlow()
//     fun onSupplierIdChange(idString: String) {
//          _supplierId.value = try { UUID.fromString(idString.trim()) } catch (e: IllegalArgumentException) { null }
//     }


//     private val _errorField = MutableStateFlow<String?>(null) // e.g., "productName", "minimumStock"
//     val errorField: StateFlow<String?> = _errorField.asStateFlow()

//     // Your existing product StateFlow and other logic
//     // For example, if you had a single StateFlow<NewProductUiState>
//     // you would update individual fields within that state.

//     private val _addProductState = MutableStateFlow<OperationState>(OperationState.Idle)
//     val addProductState: StateFlow<OperationState> = _addProductState.asStateFlow()

//     fun saveProduct() {
//         // 1. Validate all fields
//         //    If validation fails, set _errorField.value and return
//         //    Example:
//         //    if (_productCode.value.isBlank()) {
//         //        _errorField.value = "productCode"
//         //        return
//         //    }
//         //    _errorField.value = null // Clear error if all valid before proceeding
//
//         // 2. Set state to Loading
//         _addProductState.value = OperationState.Loading
//
//         // 3. Construct NewProduct object
//         //    Handle parsing of Ints and Floats from String StateFlows carefully (e.g., toIntOrNull(), toFloatOrNull())
//         //    val newProduct = NewProduct(
//         //        productCode = _productCode.value,
//         //        productName = _productName.value,
//         //        description = _description.value,
//         //        categoryId = _categoryId.value,
//         //        brand = _brand.value,
//         //        unitOfMeasure = _unitOfMeasure.value,
//         //        minimumStock = _minimumStock.value.toIntOrNull() ?: 0, // Provide default or handle error
//         //        maximumStock = _maximumStock.value.toIntOrNull() ?: 0,
//         //        costPrice = _costPrice.value.toFloatOrNull() ?: 0f,
//         //        sellingPrice = _sellingPrice.value.toFloatOrNull() ?: 0f,
//         //        markupPercentage = _markupPercentage.value.toFloatOrNull() ?: 0f,
//         //        supplierId = _supplierId.value
//         //    )
//
//         // 4. Call your repository/use case to save the product
//         // viewModelScope.launch {
//         //     try {
//         //         // yourRepository.addProduct(newProduct)
//         //         _addProductState.value = OperationState.Success
//         //     } catch (e: Exception) {
//         //         _addProductState.value = OperationState.Error("Failed to save product: ${e.message}")
//         //     }
//         // }
//     }
//
//      fun resetOperationState() {
//         _addProductState.value = OperationState.Idle
//     }


//     sealed class OperationState {
//         object Idle : OperationState()
//         object Loading : OperationState()
//         object Success : OperationState()
//         data class Error(val message: String) : OperationState()
//     }
// }

