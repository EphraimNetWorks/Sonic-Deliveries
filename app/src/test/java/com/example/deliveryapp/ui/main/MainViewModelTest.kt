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

    private val deliveries = MutableLiveData<List<Delivery>>()
    private val list = listOf(
        Delivery().apply {
            title = "1"
            origin = "Accra"
            destination = "Tema"
            deliveryStatus = Delivery.STATUS_PENDING
        },Delivery().apply {
            title = "2"
            origin = "Kumasi"
            destination = "Tema"
            deliveryStatus = Delivery.STATUS_COMPLETED
        },Delivery().apply {
            title = "3"
            origin = "Accra"
            destination = "Tema"
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
        deliveries.postValue(list)

        runBlocking {
            mainViewModel = MainViewModel(deliveryRepository, userRepository, testProvider)
        }

        //mock paged list
        val dummyLD = MutableLiveData<PagedList<Delivery>>()
        dummyLD.postValue(mockPagedList(list))
        mainViewModel.myDeliveries = dummyLD
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
    fun `validate set deliveries`(){

        assertNotNull(mainViewModel.myDeliveries)
        assertEquals("1", mainViewModel.myDeliveries!!.value?.get(0)!!.title)
        assertEquals("2", mainViewModel.myDeliveries!!.value?.get(1)!!.title)
        assertEquals("3", mainViewModel.myDeliveries!!.value?.get(2)!!.title)
    }

    @Test
    fun `set network state on get network state`(){
        assertNotNull(mainViewModel.getNetworkState().value)
    }
}