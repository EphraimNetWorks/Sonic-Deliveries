package com.example.deliveryapp.data.local.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.utils.DispatcherProvider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.capture
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import kotlinx.coroutines.Dispatchers
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify
import javax.sql.DataSource

class DeliveryRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var deliveryDao: DeliveryDao

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<ApiCallback<List<Delivery>>>

    @Captor
    lateinit var submitDeliveryCallbackCaptor: ArgumentCaptor<ApiCallback<Boolean>>

    @Captor
    lateinit var cancelDeliveryCallbackCaptor: ArgumentCaptor<ApiCallback<Boolean>>

    private lateinit var deliveryRepository: DeliveryRepository

    val testdelivery = Delivery().apply { title = "Box" }

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
        val dataSourceMock = Mockito.mock(androidx.paging.DataSource.Factory::class.java) as androidx.paging.DataSource.Factory<Int,Delivery>

        Mockito.`when`(deliveryDao.getDeliveriesPlaced()).thenReturn(dataSourceMock)
        Mockito.`when`(deliveryDao.getDeliveriesInTransit()).thenReturn(dataSourceMock)
        Mockito.`when`(deliveryDao.getCompletedDeliveries()).thenReturn(dataSourceMock)

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

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.FAILED
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

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.FAILED
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

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.FAILED
        )
    }

    @Test
    fun `set network state to loading on cancel delivery`(){

        deliveryRepository.cancelDelivery("fdjha")

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `process cancelDelivery result when negative callback called`(){
        val testDeliveryId = "kjafd"
        val errorMessage = " This is an error"
        deliveryRepository.cancelDelivery(testDeliveryId)

        verify(apiService).cancelDelivery(any(),capture(cancelDeliveryCallbackCaptor))

        val callback = cancelDeliveryCallbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.message,
            errorMessage
        )

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.FAILED
        )
    }

    @Test
    fun `process cancelDelivery result when positive callback called`(){
        val testDeliveryId = "kjafd"
        deliveryRepository.cancelDelivery(testDeliveryId)

        verify(apiService).cancelDelivery(any(),capture(cancelDeliveryCallbackCaptor))

        val callback = cancelDeliveryCallbackCaptor.value

        callback.onSuccess(true)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )
    }

    @Test
    fun `set network state to loading on submit new delivery`(){

        deliveryRepository.submitNewDelivery(testdelivery)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `process submitDelivery result when negative callback called`(){

        val errorMessage = " This is an error"
        deliveryRepository.submitNewDelivery(testdelivery)

        verify(apiService).sendNewDelivery(any(),capture(submitDeliveryCallbackCaptor))

        val callback = submitDeliveryCallbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.message,
            errorMessage
        )

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.FAILED
        )
    }

    @Test
    fun `process submitDelivery result when positivie callback called`(){

        deliveryRepository.submitNewDelivery(testdelivery)

        verify(apiService).sendNewDelivery(any(),capture(submitDeliveryCallbackCaptor))

        val callback = submitDeliveryCallbackCaptor.value

        callback.onSuccess(true)

        assertEquals(
            deliveryRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )
    }
}