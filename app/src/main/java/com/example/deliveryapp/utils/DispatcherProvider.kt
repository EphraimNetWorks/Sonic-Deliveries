package com.example.deliveryapp.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class DispatcherProvider(
    val IO: CoroutineDispatcher = Dispatchers.IO,
    val Main: CoroutineDispatcher = Dispatchers.Main,
    val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
)