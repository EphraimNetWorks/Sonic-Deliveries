package com.example.deliveryapp.di

import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.FakeDeliveryRepository
import com.example.deliveryapp.di.FakeUserRepository
import com.example.deliveryapp.di.modules.MainModule
import com.example.deliveryapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class TestMainModule{

    @Provides fun providesApiService(): ApiService {
        return Mockito.mock(ApiService::class.java)
    }


    @Provides fun providesDispactcherProvider(): DispatcherProvider {
        return DispatcherProvider(Dispatchers.Unconfined,Dispatchers.Unconfined,Dispatchers.Unconfined)
    }
}