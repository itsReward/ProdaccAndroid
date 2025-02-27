package com.example.products.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.CreateProductCategory
import com.prodacc.data.remote.dao.product.CreateProductVehicle
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ViewProductViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId: String = checkNotNull(savedStateHandle["productId"]) {
        "ProductId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }


    //State for Product
    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    // State for Categories
    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    // State for Vehicles
    private val _vehicles = MutableStateFlow<List<ProductVehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _updateProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val updateProductState = _updateProductState.asStateFlow()

    private val _deleteProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val deleteProductState = _deleteProductState.asStateFlow()

    private val _loading = MutableStateFlow<OperationState>(OperationState.Idle)
    val loading = _loading.asStateFlow()


    init {
        _loading.value = OperationState.Idle
        fetchProduct()
    }

    private fun fetchProduct() {
        viewModelScope.launch {
            _loading.value = OperationState.Loading
            try {
                productsUseCase.getProductById(UUID.fromString(productId)).collect{ resource ->
                    when(resource){
                        is Resource.Error -> _loading.value = OperationState.Error(resource.message)
                        is Resource.Loading -> _loading.value = OperationState.Loading
                        is Resource.Success -> {
                            _product.value = resource.data
                            _loading.value = OperationState.Idle
                        }
                    }
                }

            } catch (e:Exception){
                handleException(e)
            }
        }
    }



    fun updateProduct(id: UUID, updatedProduct: Product) {
        viewModelScope.launch {
            _updateProductState.value = OperationState.Loading
            try {
                productsUseCase.updateProduct(id, updatedProduct).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data.let { product ->
                                EventBus.emit(EventBus.ProductEvent.UpdateProduct)
                                _updateProductState.value = OperationState.Success(product)
                            } ?: run {
                                _updateProductState.value = OperationState.Error("No data returned")
                            }
                        }
                        is Resource.Error -> {
                            _updateProductState.value = OperationState.Error(resource.message ?: "Unknown error")
                        }
                        is Resource.Loading -> {
                            _updateProductState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _updateProductState.value = OperationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun deleteProduct(id: UUID) {
        viewModelScope.launch {
            _deleteProductState.value = OperationState.Loading
            try {
                productsUseCase.deleteProduct(id).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            EventBus.emit(EventBus.ProductEvent.DeleteProduct)
                            _deleteProductState.value = OperationState.Idle
                        }
                        is Resource.Error -> {
                            _deleteProductState.value = OperationState.Error(resource.message ?: "Unknown error")
                        }
                        is Resource.Loading -> {
                            _deleteProductState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _deleteProductState.value = OperationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Similar methods for categories and vehicles management
    fun addCategory(newCategory: CreateProductCategory) {
        viewModelScope.launch {
            try {
                productsUseCase.addNewCategory(newCategory).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { category ->
                                _categories.value = _categories.value + category
                            }
                        }
                        else -> {} // Handle other states as needed
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun addVehicle(newVehicle: CreateProductVehicle) {
        viewModelScope.launch {
            try {
                productsUseCase.addNewVehicle(newVehicle).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { vehicle ->
                                _vehicles.value = _vehicles.value + vehicle
                            }
                        }
                        else -> {} // Handle other states as needed
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    // Additional methods to manage product-category and product-vehicle relationships
    fun associateProductWithCategory(productId: UUID, categoryId: UUID) {
        // Implementation depends on your backend API
    }

    fun associateProductWithVehicle(productId: UUID, vehicleId: UUID) {
        // Implementation depends on your backend API
    }

    private fun handleException(e: Exception) {
        when (e){
            is IOException -> _loading.value = OperationState.Error("NetWork Problems: ${e.message}")
            else -> _loading.value = OperationState.Error(e.message ?:  "Unknown Error")
        }
    }

    // Operation state for CRUD operations
    sealed class OperationState {
        data object Idle : OperationState()
        data object Loading : OperationState()
        data class Success(val data: Product) : OperationState()
        data class Error(val message: String) : OperationState()
    }
}