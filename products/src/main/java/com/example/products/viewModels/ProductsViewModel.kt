package com.example.products.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductCategoryWithProduct
import com.prodacc.data.remote.dao.product.ProductVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
) : ViewModel() {
    // State for Products
    private val _categoriesWithProducts = MutableStateFlow<List<ProductCategoryWithProduct>>(emptyList())
    val categoriesWithProducts = _categoriesWithProducts.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(_products.value)
    val filteredProducts = _filteredProducts.asStateFlow()

    // State for Categories
    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    // State for Vehicles
    private val _vehicles = MutableStateFlow<List<ProductVehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    // Loading States
    private val _productsLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val productsLoadState = _productsLoadState.asStateFlow()

    private val _categoriesLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val categoriesLoadState = _categoriesLoadState.asStateFlow()

    private val _vehiclesLoadState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val vehiclesLoadState = _vehiclesLoadState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    // Refreshing state
    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    // Filter state
    private val _productFilter = MutableStateFlow<ProductFilter>(ProductFilter.All())
    val productFilter = _productFilter.asStateFlow()

    // Selected category for filtering
    private val _selectedCategoryId = MutableStateFlow<UUID?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    // Selected vehicle for filtering
    private val _selectedVehicleId = MutableStateFlow<UUID?>(null)
    val selectedVehicleId = _selectedVehicleId.asStateFlow()

    init {
        viewModelScope.launch {
            EventBus.events.collect{ event ->
                when (event){
                    EventBus.ProductEvent.DeleteProduct -> refreshData()
                    EventBus.ProductEvent.NewProduct -> refreshData()
                    EventBus.ProductEvent.UpdateProduct -> refreshData()
                }
            }
        }

        viewModelScope.launch {
            fetchCategoriesWithProducts()
            fetchProducts()
            fetchCategories()
            fetchVehicles()
        }
    }

    fun onSearchQueryChange(searchQuery: String){
        _searchQuery.value = searchQuery
        filterProducts()

    }

    fun onFilterByAll() {
        _productFilter.value = ProductFilter.All()
        _selectedCategoryId.value = null
        _selectedVehicleId.value = null
        filterProducts()
    }

    fun onFilterByCategory(categoryId: UUID) {
        _productFilter.value = ProductFilter.ByCategory()
        _selectedCategoryId.value = categoryId
        _selectedVehicleId.value = null
        filterProducts()
    }

    fun onFilterByVehicle(vehicleId: UUID) {
        _productFilter.value = ProductFilter.ByVehicle()
        _selectedVehicleId.value = vehicleId
        _selectedCategoryId.value = null
        filterProducts()
    }

    private fun filterProducts() {
        viewModelScope.launch {
            val currentProducts = _products.value
            _filteredProducts.value = currentProducts.filter {
                it.partName.contains(searchQuery.value, ignoreCase = true) || it.partNumber.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }

    private suspend fun fetchProducts() {
        _productsLoadState.value = LoadingState.Loading
        try {
            productsUseCase.getProducts().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _products.value = resource.data
                        _filteredProducts.value = resource.data
                        _productsLoadState.value = LoadingState.Success(_products.value)
                    }
                    is Resource.Error -> {
                        _productsLoadState.value = LoadingState.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _productsLoadState.value = LoadingState.Loading
                    }
                }
            }
        } catch (e: Exception) {
            _productsLoadState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
        }
    }

    private suspend fun fetchCategoriesWithProducts() {
        _productsLoadState.value = LoadingState.Loading
        try {
            productsUseCase.getCategoriesWithProducts().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _categoriesWithProducts.value = resource.data
                        _productsLoadState.value = LoadingState.Success(_products.value)
                    }
                    is Resource.Error -> {
                        _productsLoadState.value = LoadingState.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _productsLoadState.value = LoadingState.Loading
                    }
                }
            }
        } catch (e: Exception) {
            _productsLoadState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
        }
    }

    private suspend fun fetchCategories() {
        _categoriesLoadState.value = LoadingState.Loading
        try {
            productsUseCase.getCategories().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _categories.value = resource.data ?: emptyList()
                        _categoriesLoadState.value = LoadingState.Success(_categories.value)
                    }
                    is Resource.Error -> {
                        _categoriesLoadState.value = LoadingState.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _categoriesLoadState.value = LoadingState.Loading
                    }
                }
            }
        } catch (e: Exception) {
            _categoriesLoadState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
        }
    }

    private suspend fun fetchVehicles() {
        _vehiclesLoadState.value = LoadingState.Loading
        try {
            productsUseCase.getVehicleList().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _vehicles.value = resource.data ?: emptyList()
                        _vehiclesLoadState.value = LoadingState.Success(_vehicles.value)
                    }
                    is Resource.Error -> {
                        _vehiclesLoadState.value = LoadingState.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _vehiclesLoadState.value = LoadingState.Loading
                    }
                }
            }
        } catch (e: Exception) {
            _vehiclesLoadState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _refreshing.value = true
            try {
                // Clear existing data
                _categoriesWithProducts.value = emptyList()
                _products.value = emptyList()
                _categories.value = emptyList()
                _vehicles.value = emptyList()

                // Fetch fresh data
                fetchCategoriesWithProducts()
                fetchProducts()
                fetchCategories()
                fetchVehicles()
            } finally {
                _refreshing.value = false
            }
        }
    }

    // Clean up resources
    override fun onCleared() {
        super.onCleared()
        // Cleanup if needed
    }

    // Filter classes
    sealed class ProductFilter {
        data class All(val name: String = "all") : ProductFilter()
        data class ByCategory(val name: String = "byCategory") : ProductFilter()
        data class ByVehicle(val name: String = "byVehicle") : ProductFilter()
    }

    sealed class LoadingState{
        data class Success(val products: List<Any>): LoadingState()
        data class Error(val message: String): LoadingState()
        data object Loading: LoadingState()
        data object Idle: LoadingState()
    }


}
