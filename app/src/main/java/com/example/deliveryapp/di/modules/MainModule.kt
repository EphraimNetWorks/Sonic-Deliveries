package com.example.deliveryapp.di.modules

import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides

@Module
class MainModule{


    @Provides
    fun providesApiService(): ApiService {
        return ApiService()
    }

    @Provides
    fun providesDeliveryRepository(apiService: ApiService, orderDao: DeliveryDao): DeliveryRepository {
        return DeliveryRepository(apiService, orderDao)
    }

    @Provides
    fun providesUserRepository(apiService: ApiService, userDao: UserDao, localDatabase: LocalDatabase): UserRepository {
        return UserRepository(apiService,userDao,localDatabase)
    }

    @Provides
    fun providesDispactcherProvider(): DispatcherProvider {
        return DispatcherProvider()
    }


}