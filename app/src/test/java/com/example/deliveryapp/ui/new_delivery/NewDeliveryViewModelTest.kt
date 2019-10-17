package com.example.deliveryapp.ui.new_delivery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class NewDeliveryViewModelTest{


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var deliveryDao: DeliveryDao

    lateinit var deliveryRepo: DeliveryRepository

    lateinit var viewModel:NewDeliveryViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        deliveryRepo = DeliveryRepository(apiService, deliveryDao)

        viewModel = NewDeliveryViewModel(deliveryRepo)
    }

    @Test
    fun `get network state returns repo network state`(){



        val networkState = viewModel.getNetworkState()

        assertNotNull(networkState.value)

    }

    @Test
    fun `submit delivery calls repo submit delivery`(){

        val testDelivery = Delivery()

        viewModel.submitNewDelivery(testDelivery)

        Mockito.verify(apiService).sendNewDelivery(any(Delivery::class.java),com.nhaarman.mockitokotlin2.any())

    }
}