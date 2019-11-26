package com.example.deliveryapp.utils

import javax.inject.Inject
import javax.inject.Provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deliveryapp.di.scopes.ActivityScope

@ActivityScope
class ViewModelFactory @Inject
constructor(private val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider = viewModels[modelClass]
                ?: throw IllegalArgumentException("model class $modelClass not found")

        return viewModelProvider.get() as T
    }
}

