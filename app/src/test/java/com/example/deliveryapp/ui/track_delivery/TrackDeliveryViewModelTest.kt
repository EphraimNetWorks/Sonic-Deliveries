package com.example.deliveryapp.ui.track_delivery

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

class TrackDeliveryViewModelTest{

    @Mock
    lateinit var deliveryRepo: DeliveryRepository

    lateinit var viewModel:TrackDeliveryViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        viewModel = TrackDeliveryViewModel(deliveryRepo)
    }

    @Test
    fun `get network state returns repo network state`(){

        val testNetworkState = MutableLiveData(NetworkState.LOADED)

        Mockito.`when`(deliveryRepo.getNetworkState()).thenReturn(testNetworkState)

        val networkState = viewModel.getNetWorkState()

        assertEquals(networkState.value!!.status, testNetworkState.value!!.status)

    }

    @Test
    fun `cancel delivery calls repo cancel delivery`(){

        val testDelivery = Delivery().apply { id = "id" }

        viewModel.cancelDelivery(testDelivery)

        Mockito.verify(deliveryRepo).cancelDelivery(testDelivery.id)

    }
}