package com.example.deliveryapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.utils.DispatcherProvider
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var deliveryRepository: DeliveryRepository

    private lateinit var mainViewModel: MainViewModel

    private val inTransitList = listOf(
        Delivery().apply {
            title = "1"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_IN_TRANSIT
        },Delivery().apply {
            title = "2"
            pickUpAddress = "Kumasi"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_IN_TRANSIT
        },Delivery().apply {
            title = "3"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_IN_TRANSIT
        }
    )
    private val placedList = listOf(
        Delivery().apply {
            title = "1"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_PLACED
        },Delivery().apply {
            title = "2"
            pickUpAddress = "Kumasi"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_PLACED
        },Delivery().apply {
            title = "3"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_PLACED
        }
    )
    private val completedList = listOf(
        Delivery().apply {
            title = "1"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_CANCELLED
        },Delivery().apply {
            title = "2"
            pickUpAddress = "Kumasi"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_COMPLETED
        },Delivery().apply {
            title = "3"
            pickUpAddress = "Accra"
            destinationAddress = "Tema"
            deliveryStatus = Delivery.STATUS_COMPLETED
        }
    )

    private val user = User().apply {
        id = "akfjklka"
        name = "Ephraim Nartey"
        email = "narteyephraim@gmail.com"
        phone = "+233501384237"
        profilePicUrl = ""
    }

    private val testProvider = DispatcherProvider(
        IO = Dispatchers.Unconfined,
        Main = Dispatchers.Unconfined
    )

    @Before
    fun setUp(){
        MockKAnnotations.init(this)

        coEvery { userRepository.getCurrentUser() }.returns (user)

        runBlocking {
            mainViewModel = MainViewModel(deliveryRepository, userRepository, testProvider)
        }

    }

    fun <T> mockPagedList(list: List<T>): PagedList<T> {
        val pagedList = Mockito.mock(PagedList::class.java) as PagedList<T>
        Mockito.`when`(pagedList[ArgumentMatchers.anyInt()]).then { invocation ->
            val index = invocation.arguments.first() as Int
            list[index]
        }
        Mockito.`when`(pagedList.size).thenReturn(list.size)
        return pagedList
    }

    @Test
    fun `validate set current user`(){
        runBlocking {
            assertNotNull(mainViewModel.currentUser)
            assertEquals("Ephraim Nartey", mainViewModel.currentUser!!.name)
        }
    }

    @Test
    fun `validate set placed deliveries`(){

        //mock paged list
        val dummyLD = MutableLiveData<PagedList<Delivery>>()
        dummyLD.postValue(mockPagedList(placedList))
        mainViewModel.deliveriesPlaced = dummyLD

        assertNotNull(mainViewModel.deliveriesPlaced)
        assertEquals("1", mainViewModel.deliveriesPlaced!!.value?.get(0)!!.title)
        assertEquals("2", mainViewModel.deliveriesPlaced!!.value?.get(1)!!.title)
        assertEquals("3", mainViewModel.deliveriesPlaced!!.value?.get(2)!!.title)
    }

    @Test
    fun `validate set in transit deliveries`(){

        //mock paged list
        val dummyLD = MutableLiveData<PagedList<Delivery>>()
        dummyLD.postValue(mockPagedList(inTransitList))
        mainViewModel.deliveriesInTransit = dummyLD

        assertNotNull(mainViewModel.deliveriesInTransit)
        assertEquals("4", mainViewModel.deliveriesInTransit!!.value?.get(0)!!.title)
        assertEquals("5", mainViewModel.deliveriesInTransit!!.value?.get(1)!!.title)
        assertEquals("6", mainViewModel.deliveriesInTransit!!.value?.get(2)!!.title)
    }

    @Test
    fun `validate set completed deliveries`(){

        //mock paged list
        val dummyLD = MutableLiveData<PagedList<Delivery>>()
        dummyLD.postValue(mockPagedList(completedList))
        mainViewModel.completedDeliveries = dummyLD

        assertNotNull(mainViewModel.completedDeliveries)
        assertEquals("7", mainViewModel.completedDeliveries!!.value?.get(0)!!.title)
        assertEquals("8", mainViewModel.completedDeliveries!!.value?.get(1)!!.title)
        assertEquals("9", mainViewModel.completedDeliveries!!.value?.get(2)!!.title)
    }

    @Test
    fun `set network state on get network state`(){
        assertNotNull(mainViewModel.getNetworkState().value)
    }
}