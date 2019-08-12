package com.example.deliveryapp.di.modules

import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides

@Module
open class MainModule{


    @Provides
    internal open fun providesApiService(): ApiService {
        return ApiService()
    }

    @Provides
    internal open fun providesDeliveryRepository(apiService: ApiService, orderDao: DeliveryDao): DeliveryRepository {
        return DeliveryRepository(apiService, orderDao)
    }

    @Provides
    internal open fun providesUserRepository(apiService: ApiService, userDao: UserDao): UserRepository {
        return UserRepository(apiService,userDao)
    }

    @Provides
    internal open fun providesDispactcherProvider(): DispatcherProvider {
        return DispatcherProvider()
    }


}