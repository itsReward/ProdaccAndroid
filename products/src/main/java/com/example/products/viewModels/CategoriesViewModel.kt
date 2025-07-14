package com.example.products.viewModels.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.CreateProductCategory
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductCategoryWithProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
): ViewModel(){

    private val _categoriesWithProducts = MutableStateFlow<List<ProductCategoryWithProduct>>(emptyList())
    val categoriesWithProducts = _categoriesWithProducts.asStateFlow()

    private val _filteredCategoriesWithProducts = MutableStateFlow<List<ProductCategoryWithProduct>>(emptyList())
    val filteredCategoriesWithProducts = _filteredCategoriesWithProducts.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _newCategory = MutableStateFlow(CreateProductCategory("", ""))
    val newCategory = _newCategory.asStateFlow()

    private val _newCategorySavingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val newCategorySavingState = _newCategorySavingState.asStateFlow()

    private val _newCategoryBottomSheetModalExpand = MutableStateFlow(false)
    val newCategoryBottomSheetModalExpand = _newCategoryBottomSheetModalExpand.asStateFlow()

    private val _deleteCategoryState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val deleteCategoryState = _deleteCategoryState.asStateFlow()

    private val _deleteDialogState = MutableStateFlow(false)
    val deleteDialogState = _deleteDialogState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    sealed class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    init {
        refreshData()
    }

    fun refreshData(){
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            delay(1000)
            fetchCategoriesWithProducts()
        }
    }

    private suspend fun fetchCategoriesWithProducts(){
        try {
            productsUseCase.getCategoriesWithProducts().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _categoriesWithProducts.value = resource.data
                        filterCategories()
                        _loadingState.value = LoadingState.Success
                    }
                    is Resource.Error -> {
                        _loadingState.value = LoadingState.Error(resource.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {
                        _loadingState.value = LoadingState.Loading
                    }
                }
            }
        } catch (e: Exception) {
            _loadingState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun toggleSearch() {
        _searchExpanded.value = !_searchExpanded.value
        if (!_searchExpanded.value) {
            _searchQuery.value = ""
            filterCategories()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterCategories()
    }

    private fun filterCategories() {
        val query = _searchQuery.value
        _filteredCategoriesWithProducts.value = if (query.isBlank()) {
            _categoriesWithProducts.value
        } else {
            _categoriesWithProducts.value.filter { category ->
                category.name.contains(query, ignoreCase = true) ||
                        category.description?.contains(query, ignoreCase = true) == true ||
                        category.product.any { product ->
                            product.partName.contains(query, ignoreCase = true) ||
                                    product.partNumber.contains(query, ignoreCase = true)
                        }
            }
        }
    }

    fun newCategoryBottomSheetModalVisibilityToggle(){
        _newCategoryBottomSheetModalExpand.value = !_newCategoryBottomSheetModalExpand.value
        if (!_newCategoryBottomSheetModalExpand.value) {
            // Reset form when closing
            _newCategory.value = CreateProductCategory("", "")
        }
    }

    fun updateCategoryName(name: String) {
        _newCategory.value = _newCategory.value.copy(name = name)
    }

    fun updateCategoryDescription(description: String) {
        _newCategory.value = _newCategory.value.copy(description = description.takeIf { it.isNotBlank() })
    }

    fun saveNewCategory() {
        if (_newCategory.value.name.isBlank()) return

        _newCategorySavingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.addNewCategory(_newCategory.value).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _newCategorySavingState.value = LoadingState.Success
                            newCategoryBottomSheetModalVisibilityToggle()
                            refreshData() // Refresh the list
                        }
                        is Resource.Error -> {
                            _newCategorySavingState.value = LoadingState.Error(resource.message ?: "Failed to create category")
                        }
                        is Resource.Loading -> {
                            _newCategorySavingState.value = LoadingState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _newCategorySavingState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onDeleteDialogToggle() {
        _deleteDialogState.value = !_deleteDialogState.value
    }

    fun setSelectedCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun deleteCategory() {
        val category = _selectedCategory.value ?: return

        _deleteCategoryState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.deleteCategory(category.id).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _deleteCategoryState.value = LoadingState.Success
                            onDeleteDialogToggle()
                            refreshData() // Refresh the list
                        }
                        is Resource.Error -> {
                            _deleteCategoryState.value = LoadingState.Error(resource.message ?: "Failed to delete category")
                        }
                        is Resource.Loading -> {
                            _deleteCategoryState.value = LoadingState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _deleteCategoryState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}