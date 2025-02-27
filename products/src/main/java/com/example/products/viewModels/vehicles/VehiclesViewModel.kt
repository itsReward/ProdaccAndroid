package com.example.products.viewModels.vehicles

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.example.products.viewModels.EventBus
import com.prodacc.data.remote.dao.product.ProductVehicle
import com.prodacc.data.remote.dao.product.ProductVehicleWithProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
): ViewModel(){
    private val _vehicles = MutableStateFlow<List<ProductVehicle>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _filteredVehicles = MutableStateFlow<List<ProductVehicleWithProducts>>(emptyList())
    val filteredVehicle = _filteredVehicles.asStateFlow()

    private val _displayVehicles = MutableStateFlow<List<ProductVehicleWithProducts>>(emptyList())
    val displayVehicles = _displayVehicles.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {

        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            fetchVehicles()
        }
    }

    fun toggleSearch(){
        _searchExpanded.value = !_searchExpanded.value
    }

    fun onSearchQueryChange(query: String){
        _searchQuery.value = query
        filterVehicles()
    }

    private suspend fun fetchVehicles(){
        try {
            productsUseCase.getVehicleList().collect { resource ->
                when(resource){
                    is Resource.Error -> _loadingState.value = LoadingState.Error(resource.message)
                    is Resource.Loading -> _loadingState.value = LoadingState.Loading
                    is Resource.Success -> {
                        _vehicles.value = resource.data
                        fetchVehiclesWithProducts()
                        _loadingState.value = LoadingState.Success
                    }
                }
            }
        } catch (e: Exception) {
            _loadingState.value = LoadingState.Error(e.message?:"Unknown Error Occurred")
        }
    }

    private suspend fun fetchVehiclesWithProducts(){
        try {
            val displayVehicle = mutableListOf<ProductVehicleWithProducts>()
            _vehicles.value.forEach {
                productsUseCase.get().collect { resource ->
                    when(resource){
                        is Resource.Error -> _loadingState.value = LoadingState.Error(resource.message)
                        is Resource.Loading -> _loadingState.value = LoadingState.Loading
                        is Resource.Success -> {
                            _vehicles.value = resource.data
                            fetchVehiclesWithProducts()
                            _loadingState.value = LoadingState.Success
                        }
                    }
                }
            }
            _displayVehicles.value = displayVehicle
        } catch (e: Exception){
            _loadingState.value = LoadingState.Error(e.message?:"Unknown Error")
        }
    }

    private fun filterVehicles(){
        viewModelScope.launch {
            val currentVehicles = _filteredVehicles.value
            _filteredVehicles.value = currentVehicles.filter {
                it.model.contains(_searchQuery.value, ignoreCase = true) || it.make.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }



    fun refreshData() {
        viewModelScope.launch {

        }

    }

    sealed class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message : String): LoadingState()
    }
}