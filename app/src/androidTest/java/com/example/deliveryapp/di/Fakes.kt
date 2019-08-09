package com.example.deliveryapp.di

import androidx.paging.DataSource
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService

class FakeUserRepository: UserRepository(ApiService(),object : UserDao {
    override fun deleteCurrentUser(user: User) {

    }

    override fun saveUser(user: User) {
    }

    override fun getCurrentUser(): User? {
        return null
    }
})

class FakeDeliveryRepository: DeliveryRepository(ApiService(),object: DeliveryDao{
    override fun deleteMyDelivery(delivery: Delivery) {

    }

    override fun saveMyDelivery(myDelivery: Delivery) {

    }

    override fun getDeliveriesPlaced(status: Int): DataSource.Factory<Int, Delivery>? {
        return null
    }

    override fun getDeliveriesInTransit(status: Int): DataSource.Factory<Int, Delivery>? {
        return null
    }

    override fun getCompletedDeliveries(status: Int, status2: Int): DataSource.Factory<Int, Delivery>? {
        return null
    }

    override fun updateDeliveryStatus(deliveryId: String, status: Int) {

    }
})