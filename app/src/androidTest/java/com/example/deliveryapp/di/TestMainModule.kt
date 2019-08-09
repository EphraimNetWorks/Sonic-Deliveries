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
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

class TestMainModule(
    private val userRepository: UserRepository = FakeUserRepository(),
    private val detailsRepository: DeliveryRepository = FakeDeliveryRepository()): MainModule() {


    override fun providesUserRepository(apiService: ApiService, userDao: UserDao): UserRepository {
        return userRepository
    }

    override fun providesDeliveryRepository(apiService: ApiService, orderDao: DeliveryDao): DeliveryRepository {
        return detailsRepository
    }
}