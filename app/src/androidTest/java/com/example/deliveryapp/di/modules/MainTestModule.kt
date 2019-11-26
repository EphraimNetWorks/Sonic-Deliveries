package com.example.deliveryapp.di.modules

import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.ApiServiceImpl
import com.example.deliveryapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class MainTestModule{


    @Provides
    @Singleton
    fun providesApiService(): ApiService {
        return Mockito.mock(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRepository(apiService: ApiService, userDao: UserDao, localDatabase: LocalDatabase): UserRepository {
        return UserRepository(apiService,userDao,localDatabase)
    }

    @Provides fun providesDispactcherProvider(): DispatcherProvider {
        return DispatcherProvider(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined)
    }

}