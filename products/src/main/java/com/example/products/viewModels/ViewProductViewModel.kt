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
import kotlinx.coroutines.flow.MutableSharedFlow
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
) : ViewModel() {
    private val productId: String = checkNotNull(savedStateHandle["productId"]) {
        "ProductId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }


    //State for Product
    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    // State for Categories
    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _addCategoryState = MutableStateFlow<OperationState>(OperationState.Idle)
    val addCategoryState = _addCategoryState.asStateFlow()

    private val _removeCategoryState = MutableStateFlow<OperationState>(OperationState.Idle)
    val removeCategoryState = _removeCategoryState.asStateFlow()

    private val _showCategorySelectionSheet = MutableStateFlow(false)
    val showCategorySelectionSheet = _showCategorySelectionSheet.asStateFlow()

    private val _availableCategories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val availableCategories = _availableCategories.asStateFlow()

    // State for Vehicles
    private val _vehicles = MutableStateFlow<List<ProductVehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _filteredVehicles = MutableStateFlow<List<ProductVehicle>>(_vehicles.value)
    val filteredVehicles = _filteredVehicles.asStateFlow()

    private val _updateProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val updateProductState = _updateProductState.asStateFlow()

    private val _deleteProductState = MutableStateFlow<OperationState>(OperationState.Idle)
    val deleteProductState = _deleteProductState.asStateFlow()

    private val _loading = MutableStateFlow<OperationState>(OperationState.Idle)
    val loading = _loading.asStateFlow()

    private val _vehicleLoadingState = MutableStateFlow<OperationState>(OperationState.Idle)
    val vehicleLoadingState = _vehicleLoadingState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()



    init {
        _loading.value = OperationState.Idle
        fetchProduct()
        fetchVehicles()
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
        filterVehicles()
    }

    private fun filterVehicles(){
        val vehicle = _vehicles.value
        if (_searchQuery.value != ""){
            _filteredVehicles.value = vehicle.filter {
                it.model.contains(_searchQuery.value, ignoreCase = true) || it.make.contains(_searchQuery.value, ignoreCase = true)
            }
        } else {
            _filteredVehicles.value = vehicle
        }

    }

    private fun fetchProduct() {
        viewModelScope.launch {
            _loading.value = OperationState.Loading
            try {
                productsUseCase.getProductById(UUID.fromString(productId)).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _loading.value = OperationState.Error(resource.message)
                        is Resource.Loading -> _loading.value = OperationState.Loading
                        is Resource.Success -> {
                            _product.value = resource.data
                            _loading.value = OperationState.Idle
                        }
                    }
                }

            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun fetchVehicles() {
        _vehicleLoadingState.value = OperationState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.getVehicleList().collect { resource ->
                    when (resource) {
                        is Resource.Error -> _vehicleLoadingState.value =
                            OperationState.Error(resource.message)

                        is Resource.Loading -> _vehicleLoadingState.value = OperationState.Loading
                        is Resource.Success -> {
                            _vehicleLoadingState.value = OperationState.Success(resource.data)

                            /*_vehicles.value = resource.data.filter { !_product.value?.vehicles?.contains(it)!!
                                ?: false }
                            _filteredVehicles.value = _vehicles.value*/
                        }
                    }
                }
            } catch (e: Exception) {
                _vehicleLoadingState.value = OperationState.Error(e.message ?: "Unknown Error")
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
                            _updateProductState.value =
                                OperationState.Error(resource.message ?: "Unknown error")
                        }

                        is Resource.Loading -> {
                            _updateProductState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _updateProductState.value =
                    OperationState.Error(e.message ?: "Unknown error occurred")
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
                            _deleteProductState.value =
                                OperationState.Error(resource.message ?: "Unknown error")
                        }

                        is Resource.Loading -> {
                            _deleteProductState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _deleteProductState.value =
                    OperationState.Error(e.message ?: "Unknown error occurred")
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

    fun toggleCategorySelectionSheet() {
        _showCategorySelectionSheet.value = !_showCategorySelectionSheet.value
    }

    fun addCategoryToProduct(categoryId: UUID) {
        val currentProduct = _product.value ?: return

        _addCategoryState.value = OperationState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.addCategoryToProduct(currentProduct.productId, categoryId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _addCategoryState.value = OperationState.Success(resource)
                            // Refresh product data to get updated categories
                            fetchProduct()
                        }
                        is Resource.Error -> {
                            _addCategoryState.value = OperationState.Error(resource.message ?: "Failed to add category")
                        }
                        is Resource.Loading -> {
                            _addCategoryState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _addCategoryState.value = OperationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun removeCategoryFromProduct(categoryId: UUID) {
        val currentProduct = _product.value ?: return

        _removeCategoryState.value = OperationState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.removeCategoryFromProduct(currentProduct.productId, categoryId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _removeCategoryState.value = OperationState.Success(resource)
                            // Refresh product data to get updated categories
                            fetchProduct()
                        }
                        is Resource.Error -> {
                            _removeCategoryState.value = OperationState.Error(resource.message ?: "Failed to remove category")
                        }
                        is Resource.Loading -> {
                            _removeCategoryState.value = OperationState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _removeCategoryState.value = OperationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                productsUseCase.getCategories().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _categories.value = resource.data ?: emptyList()
                        }
                        is Resource.Error -> {
                            // Handle error if needed
                        }
                        is Resource.Loading -> {
                            // Handle loading if needed
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun associateProductWithVehicle(productId: UUID, vehicleId: UUID) {
        _updateProductState.value = OperationState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.addVehicleToProduct(productId, vehicleId).collect { resource ->
                    when(resource){
                        is Resource.Error -> _updateProductState.value = OperationState.Error(resource.message)
                        is Resource.Loading -> _updateProductState.value = OperationState.Loading
                        is Resource.Success -> {
                            _updateProductState.value = OperationState.Success(resource)
                        }
                    }
                }
            } catch (e: Exception) {
                _updateProductState.value = OperationState.Error(e.message?:"Unable to add vehicle to product")
            }
        }
    }

    private fun handleException(e: Exception) {
        when (e) {
            is IOException -> _loading.value =
                OperationState.Error("NetWork Problems: ${e.message}")

            else -> _loading.value = OperationState.Error(e.message ?: "Unknown Error")
        }
    }

    fun refetchProduct() {
        _loading.value = OperationState.Loading
        _product.value = null
        fetchProduct()
    }

    // Operation state for CRUD operations
    sealed class OperationState {
        data object Idle : OperationState()
        data object Loading : OperationState()
        data class Success(val data: Any) : OperationState()
        data class Error(val message: String) : OperationState()
    }
}