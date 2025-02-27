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
import javax.inject.Inject

@HiltViewModel
class NewProductViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
): ViewModel() {
    // Operation States
    private val _addProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val addProductState = _addProductState.asStateFlow()

    private val _product = MutableStateFlow(NewProduct())
    val product = _product.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _inStock = MutableStateFlow("")
    val inStock = _inStock.asStateFlow()

    private val _healthyNumber = MutableStateFlow("")
    val healthyNumber = _healthyNumber.asStateFlow()

    private val _arrivalPrice = MutableStateFlow("")
    val arrivalPrice = _arrivalPrice.asStateFlow()

    private val _sellingPrice = MutableStateFlow("")
    val sellingPrice = _sellingPrice.asStateFlow()

    val errorMessage = "Required Field"

    fun updateProduct(product: NewProduct){
        _product.value = product
    }

    fun onChangePartNumber(partNumber: String){
        _product.value = _product.value.copy(partNumber = partNumber)
    }

    fun onChangePartName(partName:String){
        _product.value = _product.value.copy(partName = partName)
    }

    fun onDescriptionChange(description: String){
        _product.value = _product.value.copy(description = description)
    }

    fun onInStockChange(inStock : String){
        _inStock.value = inStock

    }

    fun onHealthyNumberChange(healthyNumber: String){
        _healthyNumber.value = healthyNumber
    }

    fun onArrivalPriceChange(arrivalPrice: String){
        _arrivalPrice.value = arrivalPrice
    }

    fun onSellingPriceChange(sellingPrice: String){
        _sellingPrice.value = sellingPrice
    }

    fun onStorageLocationChange(storageLocation: String){
        _product.value = _product.value.copy(storageLocation = storageLocation)
    }

    private fun onCheckValidation(){
        _error.value = if (_product.value.partNumber==null) "partNumber"
        else if (_product.value.partName==null) "partName"
        else if (_product.value.description==null) "description"
        else if (_product.value.storageLocation==null) "storageLocation"
        else "noError"

        try { updateProduct(_product.value.copy(inStock = _inStock.value.toFloat()))} catch (e: Exception) {_error.value = "inStock"}
        try { updateProduct(_product.value.copy(healthyNumber = _healthyNumber.value.toFloat())) } catch (e: Exception) {_error.value = "healthyNumber"}
        try { updateProduct(_product.value.copy(arrivalPrice = _arrivalPrice.value.toFloat())) } catch (e: Exception) {_error.value = "arrivalPrice"}
        try { updateProduct(_product.value.copy(sellingPrice = _sellingPrice.value.toFloat())) } catch (e:Exception){ _error.value = "sellingPrice" }

    }
    fun saveProduct(){
        onCheckValidation()
        if (_error.value == "noError"){
            addProduct(_product.value)
        }
    }


    private fun addProduct(newProduct: NewProduct) {
        viewModelScope.launch {
            _addProductState.value = OperationState.Loading
            try {
                productsUseCase.addNewProduct(newProduct).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data.let { product ->
                                EventBus.emit(EventBus.ProductEvent.NewProduct)
                                _product.value = NewProduct()
                                _addProductState.value = OperationState.Success(product)
                            } ?: run {
                                _addProductState.value = OperationState.Error("No data returned")
                            }
                        }
                        is Resource.Error -> {
                            _addProductState.value = OperationState.Error(resource.message ?: "Unknown error")
                        }
                        is Resource.Loading -> {
                            _addProductState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _addProductState.value = OperationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class OperationState {
        data object Idle : OperationState()
        data object Loading : OperationState()
        data class Success(val data: Product) : OperationState()
        data class Error(val message: String) : OperationState()
    }
}