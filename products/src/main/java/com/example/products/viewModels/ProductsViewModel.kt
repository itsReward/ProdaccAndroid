package com.example.products.viewModels

import androidx.lifecycle.ViewModel
import com.example.products.data.ProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: ProductsUseCase
) : ViewModel() {

}
