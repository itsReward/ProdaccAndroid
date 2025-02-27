package com.example.products.viewModels

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<ProductEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: ProductEvent){
        _events.emit(event)
    }

    sealed class ProductEvent{
        data object NewProduct: ProductEvent()
        data object DeleteProduct: ProductEvent()
        data object UpdateProduct: ProductEvent()
    }
}