package com.example.products.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.data.InventoryTransactionsUseCase
import com.example.products.data.ProductsUseCase
import com.example.products.data.Resource
import com.prodacc.data.remote.dao.product.InventoryTransaction
import com.prodacc.data.remote.dao.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryTransactionViewModel @Inject constructor(
    private val inventoryTransactionsUseCase: InventoryTransactionsUseCase,
    private val productsUseCase: ProductsUseCase
) : ViewModel() {

    private val _inventoryTransactions = MutableStateFlow<List<InventoryTransaction>>(emptyList())
    val inventoryTransactions = _inventoryTransactions.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingState = _loadingState.asStateFlow()

    private val _selectedProductInventoryTransactions = MutableStateFlow<List<InventoryTransaction>>(emptyList())
    val selectedProductInventoryTransactions = _selectedProductInventoryTransactions.asStateFlow()

    private val _selectedProductInventoryTransactionsLoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val selectedProductInventoryTransactionsLoadingState = _selectedProductInventoryTransactionsLoadingState.asStateFlow()

    private val _fetchProductsLoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val fetchProductsLoadingState = _fetchProductsLoadingState.asStateFlow()

    private val _filterState = MutableStateFlow<Boolean>(false)
    val filterState = _filterState.asStateFlow()

    private val _productDrawerState = MutableStateFlow<Boolean>(false)
    val productDrawerState = _productDrawerState.asStateFlow()

    init {
        fetchInventoryTransactions()
        fetchProducts()
    }

    private fun fetchInventoryTransactions() {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                inventoryTransactionsUseCase.getInventoryTransactions().collect {
                    transactions -> when (transactions) {
                        is Resource.Success -> {
                            _inventoryTransactions.value = transactions.data
                            _loadingState.value = LoadingState.Success(_inventoryTransactions.value)
                        }
                        is Resource.Error -> {
                            _loadingState.value = LoadingState.Error(transactions.message)
                        }
                        is Resource.Loading -> {
                            _loadingState.value = LoadingState.Loading
                        }
                    }
                }

            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Unknown error")
            }
        }

    }

    private fun fetchProducts() {
        _fetchProductsLoadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                productsUseCase.getProducts().collect { products ->
                    when (products) {
                        is Resource.Success -> {
                            _products.value = products.data
                            _fetchProductsLoadingState.value = LoadingState.Success(_products.value)
                        }

                        is Resource.Error -> {
                            _fetchProductsLoadingState.value = LoadingState.Error(products.message)
                        }

                        is Resource.Loading -> {
                            _fetchProductsLoadingState.value = LoadingState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _fetchProductsLoadingState.value = LoadingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun fetchSelectedProductInventoryTransactions() {
        _selectedProductInventoryTransactionsLoadingState.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                inventoryTransactionsUseCase.getInventoryTransactionsByProductId(_selectedProduct.value!!.productId).collect {
                    transactions ->
                    when (transactions) {
                        is Resource.Success -> {
                            _selectedProductInventoryTransactions.value = transactions.data
                            _selectedProductInventoryTransactionsLoadingState.value = LoadingState.Success(_selectedProductInventoryTransactions.value)
                        }
                        is Resource.Error -> {
                            _selectedProductInventoryTransactionsLoadingState.value = LoadingState.Error(transactions.message)
                        }
                        is Resource.Loading -> {
                            _selectedProductInventoryTransactionsLoadingState.value = LoadingState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _selectedProductInventoryTransactionsLoadingState.value = LoadingState.Error(e.message ?: "Unknown error")
            }
        }
    }



    fun expandProductsDrawer(){
        _filterState.value = true
    }

    fun collapseProductsDrawer(){
        _filterState.value = false
    }

    fun reload(){
        _loadingState.value = LoadingState.Loading
        _inventoryTransactions.value = emptyList()
        fetchInventoryTransactions()
    }

    sealed class LoadingState{
        data class Success(val products: List<Any>): LoadingState()
        data class Error(val message: String): LoadingState()
        data object Loading: LoadingState()
    }


}