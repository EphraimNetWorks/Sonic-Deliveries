package com.example.deliveryapp.data.local.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.utils.DispatcherProvider
import com.nhaarman.mockito_kotlin.capture
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import kotlinx.coroutines.Dispatchers
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

class DeliveryRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var deliveryDao: DeliveryDao

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<ApiCallback<List<Delivery>>>

    private lateinit var deliveryRepository: DeliveryRepository

    private val testDispatcherProvider = DispatcherProvider(Dispatchers.Unconfined,Dispatchers.Unconfined)

    private val list = listOf(
        Delivery().apply {
            title = "1"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_IN_TRANSIT
        }, Delivery().apply {
            title = "2"
            pickUpAddress = "Kumasi"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_COMPLETED
        }, Delivery().apply {
            title = "3"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_COMPLETED
        }
    )

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        deliveryRepository =  DeliveryRepository(apiService,deliveryDao)

    }

    @Test
    fun `set network state to loading on get placed deliveries`(){

        deliveryRepository.getDeliveriesPlaced()

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `set network state to loading on get in transit deliveries`(){

        deliveryRepository.getDeliveriesInTransit()

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `set network state to loading on get completed deliveries`(){

        deliveryRepository.getCompletedDeliveries()

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `process getPlaceddeliveries result when positive callback called`(){
        deliveryRepository.getDeliveriesPlaced(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onSuccess(list)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )
    }

    @Test
    fun `process getPlaceddeliveries result when negative callback called`(){
        val errorMessage = " This is an error"
        deliveryRepository.getDeliveriesPlaced(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.message,
            errorMessage
        )
    }

    @Test
    fun `process getInTransitdeliveries result when positive callback called`(){
        deliveryRepository.getDeliveriesInTransit(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onSuccess(list)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )
    }

    @Test
    fun `process getInTransitdeliveries result when negative callback called`(){
        val errorMessage = " This is an error"
        deliveryRepository.getDeliveriesInTransit(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.message,
            errorMessage
        )
    }

    @Test
    fun `process getCompleteddeliveries result when positive callback called`(){
        deliveryRepository.getCompletedDeliveries(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onSuccess(list)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )
    }

    @Test
    fun `process getCompleteddeliveries result when negative callback called`(){
        val errorMessage = " This is an error"
        deliveryRepository.getCompletedDeliveries(testDispatcherProvider)

        verify(apiService).loadMyDeliveries(capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.message,
            errorMessage
        )
    }
}