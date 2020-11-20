package com.example.deliveryapp.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


data class DispatcherProvider(
    val IO: CoroutineDispatcher = Dispatchers.IO,
    val Main: CoroutineDispatcher = Dispatchers.Main,
    val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
)