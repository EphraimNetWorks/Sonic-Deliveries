package com.example.deliveryapp.di.modules

import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.ViewModelKey
import com.example.deliveryapp.di.scopes.ActivityScope
import com.example.deliveryapp.ui.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class DeliveryModule {

    @ActivityScope
    @Provides
    fun providesDeliveryRepository(apiService: ApiService, orderDao: DeliveryDao): DeliveryRepository {
        return DeliveryRepository(apiService, orderDao)
    }
}