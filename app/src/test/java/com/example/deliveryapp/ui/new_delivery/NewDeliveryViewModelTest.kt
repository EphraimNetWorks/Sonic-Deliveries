package com.example.deliveryapp.ui.new_delivery

import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class NewDeliveryViewModelTest{

    @Mock
    lateinit var deliveryRepo:DeliveryRepository

    lateinit var viewModel:NewDeliveryViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        viewModel = NewDeliveryViewModel(deliveryRepo)
    }

    @Test
    fun `get network state returns repo network state`(){

        val testNetworkState = MutableLiveData(NetworkState.LOADED)

        Mockito.`when`(deliveryRepo.getNetworkState()).thenReturn(testNetworkState)

        val networkState = viewModel.getNetworkState()

        assertEquals(networkState.value!!.status, testNetworkState.value!!.status)

    }

    @Test
    fun `submit delivery calls repo submit delivery`(){

        val testDelivery = Delivery()

        viewModel.submitNewDelivery(testDelivery)

        Mockito.verify(deliveryRepo).submitNewDelivery(testDelivery)

    }
}