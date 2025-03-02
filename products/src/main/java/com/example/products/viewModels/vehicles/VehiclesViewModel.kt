package com.example.products.viewModels.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.CreateProductVehicle
import com.prodacc.data.remote.dao.product.ProductVehicleWithProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
): ViewModel(){
    private val _vehicles = MutableStateFlow<List<ProductVehicleWithProducts>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _filteredVehicles = MutableStateFlow<List<ProductVehicleWithProducts>>(emptyList())
    val filteredVehicle = _filteredVehicles.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _newVehicle = MutableStateFlow<CreateProductVehicle>(CreateProductVehicle())
    val newVehicle = _newVehicle.asStateFlow()

    private val _newVehicleSavingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newVehicleSavingState = _newVehicleSavingState.asStateFlow()

    private val _newVehicleBottomSheetModalExpand = MutableStateFlow(false)
    val newVehicleBottomSheetModalExpand = _newVehicleBottomSheetModalExpand.asStateFlow()

    init {

        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            fetchVehicles()
        }
    }

    private fun updateNewVehicle(createProductVehicle: CreateProductVehicle){
        _newVehicle.value = createProductVehicle
    }

    fun newVehicleBottomSheetModalVisibiltyToggle(){
        _newVehicleBottomSheetModalExpand.value = !_newVehicleBottomSheetModalExpand.value
    }

    fun onMakeChange(make : String){
        updateNewVehicle(_newVehicle.value.copy(make = make))
    }

    fun onModelChange(model : String){
        updateNewVehicle(_newVehicle.value.copy(model = model))
    }

    fun onYearChange(year : String){
        try {
            if  (year.length <= 4) {
                updateNewVehicle(_newVehicle.value.copy(year = year.toInt()))
            }
        } catch (e: Exception){
            _newVehicleSavingState.value = LoadingState.Error("Invalid year Format put eg. 2019 no spaces")
        }
    }

    fun onSave(){
        if (
            _newVehicle.value.model == null || _newVehicle.value.make == null || _newVehicle.value.year == null
        ) {
            _newVehicleSavingState.value = LoadingState.Error("Invalid Inputs")
        } else {
            _newVehicleSavingState.value = LoadingState.Loading
            viewModelScope.launch {
                createNewVehicle()
            }
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
            productsUseCase.getVehiclesWithProducts().collect { resource ->
                when(resource){
                    is Resource.Error -> _loadingState.value = LoadingState.Error(resource.message)
                    is Resource.Loading -> _loadingState.value = LoadingState.Loading
                    is Resource.Success -> {
                        _vehicles.value = resource.data
                        _filteredVehicles.value = resource.data
                        _loadingState.value = LoadingState.Success
                    }
                }
            }
        } catch (e: Exception) {
            _loadingState.value = LoadingState.Error(e.message?:"Unknown Error Occurred")
        }
    }

    private suspend fun createNewVehicle(){
        try {
            productsUseCase.addNewVehicle(_newVehicle.value).collect { resource ->
                when (resource){
                    is Resource.Error -> _newVehicleSavingState.value = LoadingState.Error(resource.message)
                    is Resource.Loading -> _newVehicleSavingState.value = LoadingState.Loading
                    is Resource.Success -> {
                        _newVehicleSavingState.value = LoadingState.Success
                        _newVehicleBottomSheetModalExpand.value = false
                        refreshData()
                    }
                }
            }
        } catch (e: Exception){
            _newVehicleSavingState.value = LoadingState.Error(e.message?:"Unknown Error Occurred")
        }
    }


    private fun filterVehicles(){
        viewModelScope.launch {
            val currentVehicles = _filteredVehicles.value
            if (_searchQuery.value == ""){
                _filteredVehicles.value = currentVehicles
            }else {
                _filteredVehicles.value = currentVehicles.filter {
                    it.model.contains(_searchQuery.value, ignoreCase = true) || it.make.contains(searchQuery.value, ignoreCase = true)
                }

            }
        }
    }



    fun refreshData() {
        viewModelScope.launch {

        }

    }

    fun resetSavingState() {
        _newVehicleSavingState.value = LoadingState.Idle
    }

    sealed class LoadingState{
        data object Idle: LoadingState()
        data object Loading: LoadingState()
        data object Success: LoadingState()
        data class Error(val message : String): LoadingState()
    }
}