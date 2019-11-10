package com.example.deliveryapp.ui.track_delivery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class TrackDeliveryViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var deliveryDao: DeliveryDao

    lateinit var deliveryRepo: DeliveryRepository

    lateinit var viewModel:TrackDeliveryViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        deliveryRepo = DeliveryRepository(apiService,deliveryDao)

        viewModel = TrackDeliveryViewModel(deliveryRepo)
    }

    @Test
    fun `get network state returns repo network state`(){

        val networkState = viewModel.getNetWorkState()

        assertNotNull(networkState)

    }

    @Test
    fun `cancel delivery calls repo cancel delivery`(){

        val testDelivery = Delivery().apply { id = "id" }

        viewModel.cancelDelivery(testDelivery)

        Mockito.verify(apiService).cancelDelivery(anyString(),com.nhaarman.mockitokotlin2.any())

    }
}