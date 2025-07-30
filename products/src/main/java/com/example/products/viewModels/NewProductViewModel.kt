package com.example.products.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.NewProduct
import com.prodacc.data.remote.dao.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID // Import UUID
import javax.inject.Inject

@HiltViewModel
class NewProductViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
) : ViewModel() {
    // Operation States
    private val _addProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val addProductState = _addProductState.asStateFlow()

    // Holds the complete NewProduct object being built
    private val _product = MutableStateFlow(
        NewProduct( // Initialize with default non-null values for required fields
            productCode = "",
            productName = "",
            minimumStock = 0, // Default or consider making these string inputs too
            maximumStock = 0,
            costPrice = 0f,
            sellingPrice = 0f,
            markupPercentage = 0f
        )
    )
    val product = _product.asStateFlow()

    // StateFlows for string inputs that need conversion/validation
    // It's often cleaner to manage all text field inputs as strings initially
    private val _productCodeString = MutableStateFlow("")
    val productCodeString = _productCodeString.asStateFlow()

    private val _productNameString = MutableStateFlow("")
    val productNameString = _productNameString.asStateFlow()

    private val _descriptionString = MutableStateFlow("") // For optional field, can be empty
    val descriptionString = _descriptionString.asStateFlow()

    private val _categoryIdString = MutableStateFlow("")
    val categoryIdString = _categoryIdString.asStateFlow()

    private val _brandString = MutableStateFlow("")
    val brandString = _brandString.asStateFlow()

    private val _unitOfMeasureString = MutableStateFlow("")
    val unitOfMeasureString = _unitOfMeasureString.asStateFlow()

    private val _minimumStockString = MutableStateFlow("")
    val minimumStockString = _minimumStockString.asStateFlow()

    private val _maximumStockString = MutableStateFlow("")
    val maximumStockString = _maximumStockString.asStateFlow()

    private val _costPriceString = MutableStateFlow("")
    val costPriceString = _costPriceString.asStateFlow()

    private val _sellingPriceString = MutableStateFlow("")
    val sellingPriceString = _sellingPriceString.asStateFlow()

    private val _markupPercentageString = MutableStateFlow("")
    val markupPercentageString = _markupPercentageString.asStateFlow()

    private val _supplierIdString = MutableStateFlow("")
    val supplierIdString = _supplierIdString.asStateFlow()

    // Error state for individual fields. Key is field name, value is error message or boolean.
    // Using a single string to indicate the field with the first error found.
    private val _errorField = MutableStateFlow<String?>(null)
    val errorField = _errorField.asStateFlow()

    val errorMessage = "Required Field" // Can be used generally or per field

    // --- onChange functions for each input field ---

    fun onProductCodeChange(code: String) {
        _productCodeString.value = code
        if (_errorField.value == "productCode") _errorField.value = null // Clear error on change
    }

    fun onProductNameChange(name: String) {
        _productNameString.value = name
        if (_errorField.value == "productName") _errorField.value = null
    }

    fun onDescriptionChange(description: String) {
        _descriptionString.value = description
    }

    fun onCategoryIdChange(id: String) {
        _categoryIdString.value = id
        // Optionally validate UUID format as user types or on blur
    }

    fun onBrandChange(brand: String) {
        _brandString.value = brand
    }

    fun onUnitOfMeasureChange(unit: String) {
        _unitOfMeasureString.value = unit
    }

    fun onMinimumStockChange(stock: String) {
        _minimumStockString.value = stock.filter { it.isDigit() } // Allow only digits
        if (_errorField.value == "minimumStock") _errorField.value = null
    }

    fun onMaximumStockChange(stock: String) {
        _maximumStockString.value = stock.filter { it.isDigit() }
        if (_errorField.value == "maximumStock") _errorField.value = null
    }

    fun onCostPriceChange(price: String) {
        _costPriceString.value = price // Allow decimal point, handle validation later
        if (_errorField.value == "costPrice") _errorField.value = null
    }

    fun onSellingPriceChange(price: String) {
        _sellingPriceString.value = price
        if (_errorField.value == "sellingPrice") _errorField.value = null
    }

    fun onMarkupPercentageChange(percentage: String) {
        _markupPercentageString.value = percentage
        if (_errorField.value == "markupPercentage") _errorField.value = null
    }

    fun onSupplierIdChange(id: String) {
        _supplierIdString.value = id
    }

    private fun validateAndBuildProduct(): NewProduct? {
        _errorField.value = null // Reset errors

        val productCode = _productCodeString.value.trim()
        if (productCode.isEmpty()) {
            _errorField.value = "productCode"
            return null
        }

        val productName = _productNameString.value.trim()
        if (productName.isEmpty()) {
            _errorField.value = "productName"
            return null
        }

        val minimumStock = _minimumStockString.value.toIntOrNull()
        if (minimumStock == null) {
            _errorField.value = "minimumStock"
            return null
        }

        val maximumStock = _maximumStockString.value.toIntOrNull()
        if (maximumStock == null) {
            _errorField.value = "maximumStock"
            return null
        }
        if (maximumStock < minimumStock) {
            _errorField.value = "maximumStock" // Or a more specific error like "maxStockLessThanMin"
            // You might want a different error message here.
            return null
        }


        val costPrice = _costPriceString.value.toFloatOrNull()
        if (costPrice == null) {
            _errorField.value = "costPrice"
            return null
        }

        val sellingPrice = _sellingPriceString.value.toFloatOrNull()
        if (sellingPrice == null) {
            _errorField.value = "sellingPrice"
            return null
        }

        val markupPercentage = _markupPercentageString.value.toFloatOrNull()
        if (markupPercentage == null) {
            _errorField.value = "markupPercentage"
            return null
        }

        val categoryId = _categoryIdString.value.trim().let {
            if (it.isEmpty()) null else try { UUID.fromString(it) } catch (e: IllegalArgumentException) {
                _errorField.value = "categoryId"
                return null // Invalid UUID format
            }
        }
        // If an error occurred with categoryId conversion and it was set, return null
        if (_errorField.value == "categoryId") return null


        val supplierId = _supplierIdString.value.trim().let {
            if (it.isEmpty()) null else try { UUID.fromString(it) } catch (e: IllegalArgumentException) {
                _errorField.value = "supplierId"
                return null // Invalid UUID format
            }
        }
        // If an error occurred with supplierId conversion and it was set, return null
        if (_errorField.value == "supplierId") return null

        return NewProduct(
            productCode = productCode,
            productName = productName,
            description = _descriptionString.value.trim().takeIf { it.isNotEmpty() },
            categoryId = categoryId,
            brand = _brandString.value.trim().takeIf { it.isNotEmpty() },
            unitOfMeasure = _unitOfMeasureString.value.trim().takeIf { it.isNotEmpty() },
            minimumStock = minimumStock,
            maximumStock = maximumStock,
            costPrice = costPrice,
            sellingPrice = sellingPrice,
            markupPercentage = markupPercentage,
            supplierId = supplierId
        )
    }

    fun saveProduct() {
        val newProductInstance = validateAndBuildProduct()
        if (newProductInstance != null) {
            addProduct(newProductInstance)
        }
        // If newProductInstance is null, _errorField is already set by validateAndBuildProduct()
        // and the UI will react to it.
    }

    private fun addProduct(productToSave: NewProduct) {
        viewModelScope.launch {
            _addProductState.value = OperationState.Loading
            try {
                // Assuming productsUseCase.addNewProduct returns Flow<Resource<Product>>
                productsUseCase.addNewProduct(productToSave).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val returnedProduct = resource.data
                            if (returnedProduct != null) {
                                // EventBus.emit(EventBus.ProductEvent.NewProduct) // If you use EventBus
                                clearForm() // Clear inputs on success
                                _addProductState.value = OperationState.Success(returnedProduct)
                            } else {
                                _addProductState.value = OperationState.Error("No data returned from server.")
                            }
                        }
                        is Resource.Error -> {
                            _addProductState.value = OperationState.Error(resource.message ?: "Unknown error occurred while adding product.")
                        }
                        is Resource.Loading -> {
                            _addProductState.value = OperationState.Loading // Handled by initial state, but good practice
                        }
                    }
                }
            } catch (e: Exception) {
                _addProductState.value = OperationState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    private fun clearForm() {
        _productCodeString.value = ""
        _productNameString.value = ""
        _descriptionString.value = ""
        _categoryIdString.value = ""
        _brandString.value = ""
        _unitOfMeasureString.value = ""
        _minimumStockString.value = ""
        _maximumStockString.value = ""
        _costPriceString.value = ""
        _sellingPriceString.value = ""
        _markupPercentageString.value = ""
        _supplierIdString.value = ""
        _errorField.value = null
        // Also reset the _product state if it was used for anything other than being the target of the form
        _product.value = NewProduct(productCode = "", productName = "", minimumStock = 0, maximumStock = 0, costPrice = 0f, sellingPrice = 0f, markupPercentage = 0f)
    }

    fun resetOperationState() {
        _addProductState.value = OperationState.Idle
    }

    // Sealed class for operation state (Success should ideally carry the created Product)
    sealed class OperationState {
        data object Idle : OperationState()
        data object Loading : OperationState()
        data class Success(val data: Product) : OperationState() // Assuming Product is the type returned on success
        data class Error(val message: String) : OperationState()
    }
}
