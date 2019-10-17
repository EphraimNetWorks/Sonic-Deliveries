package com.example.deliveryapp.di
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.paging.DataSource
//import com.example.deliveryapp.data.local.LocalDatabase
//import com.example.deliveryapp.data.local.dao.DeliveryDao
//import com.example.deliveryapp.data.local.dao.UserDao
//import com.example.deliveryapp.data.local.entities.Delivery
//import com.example.deliveryapp.data.local.entities.User
//import com.example.deliveryapp.data.local.repository.DeliveryRepository
//import com.example.deliveryapp.data.local.repository.UserRepository
//import com.example.deliveryapp.data.remote.ApiService
//import com.example.deliveryapp.data.remote.ApiService_Impl
//import com.example.deliveryapp.utils.MockRoomDataSource
//import org.mockito.Mockito
//
//class FakeUserRepository(currentUser:User? = null): UserRepository(ApiService_Impl(),object : UserDao {
//    override fun deleteUser(user: User) {
//
//    }
//
//    override fun saveUser(user: User) {
//    }
//
//    override fun getCurrentUser(): LiveData<User>? {
//        return MutableLiveData(currentUser)
//    }
//},Mockito.mock(LocalDatabase::class.java))
//
//class FakeDeliveryRepository: DeliveryRepository(ApiService_Impl(),object: DeliveryDao{
//
//    private val placedDeliveries = listOf(
//        Delivery().apply {
//            id = "1"
//            title = "One"
//            createdAt = 1565434531000
//            updatedAt = 1565434531000
//            deliveryStatus = Delivery.STATUS_PLACED},
//        Delivery().apply {
//            id = "2"
//            title = "Two"
//            createdAt = 1565435531000
//            updatedAt = 1565435531000
//            deliveryStatus = Delivery.STATUS_PLACED})
//    private val inTransitDeliveries = listOf(
//        Delivery().apply {
//            id = "3"
//            title = "Three"
//            createdAt = 1565435531000
//            updatedAt = 1565436531000
//            deliveryStatus = Delivery.STATUS_IN_TRANSIT},
//        Delivery().apply {
//            id = "3.5"
//            title = "Three And Half"
//            createdAt = 1565435531000
//            updatedAt = 1565436531000
//            deliveryStatus = Delivery.STATUS_IN_TRANSIT})
//    private val completedDeliveries = listOf(
//        Delivery().apply {
//            id = "4"
//            title = "Four"
//            createdAt = 1565435531000
//            updatedAt = 1565437531000
//            deliveryStatus = Delivery.STATUS_COMPLETED},
//        Delivery().apply {
//            id = "5"
//            title = "Five"
//            createdAt = 1565435531000
//            updatedAt = 1565438531000
//            deliveryStatus = Delivery.STATUS_COMPLETED},
//        Delivery().apply {
//            id = "6"
//            title = "Six"
//            createdAt = 1565435531000
//            updatedAt = 1565439531000
//            deliveryStatus = Delivery.STATUS_CANCELLED})
//
//    override fun deleteMyDelivery(delivery: Delivery) {
//
//    }
//
//    override fun saveMyDelivery(myDelivery: Delivery) {
//
//    }
//
//    override fun getMyDelivery(deliveryId: String): LiveData<Delivery> {
//
//        return MutableLiveData()
//    }
//
//    override fun cancelDelivery(deliveryId: String, cancelledTime:Long) {
//
//    }
//
//    override fun getDeliveriesPlaced(): DataSource.Factory<Int, Delivery>? {
//        return MockRoomDataSource.mockDataSourceFactory(placedDeliveries)
//    }
//
//    override fun getDeliveriesInTransit(): DataSource.Factory<Int, Delivery>? {
//        return MockRoomDataSource.mockDataSourceFactory(inTransitDeliveries)
//    }
//
//    override fun getCompletedDeliveries(): DataSource.Factory<Int, Delivery>? {
//        return MockRoomDataSource.mockDataSourceFactory(completedDeliveries)
//    }
//})