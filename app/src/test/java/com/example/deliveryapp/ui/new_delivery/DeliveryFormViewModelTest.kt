package com.example.deliveryapp.ui.new_delivery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.Location
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.ApiService
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class DeliveryFormViewModelTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var deliveryDao: DeliveryDao

    lateinit var deliveryRepo: DeliveryRepository

    lateinit var viewModel :DeliveryFormViewModel

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        deliveryRepo = DeliveryRepository(apiService,deliveryDao)

        viewModel = DeliveryFormViewModel(deliveryRepo)

    }

    @Test
    fun `set initial validation map on init view model`() {
        assertNotNull(viewModel.validationMap)

        viewModel.validationMap.values.forEach { assertEquals(DeliveryFormViewModel.VAL_DEFAULT,it) }
    }

    @Test
    fun `set initial direction results on init view model`() {
        assertNotNull(viewModel.directionsResult)

    }

    @Test
    fun `set empty itemname error on validation when itemname is empty`(){
        viewModel.validateItemName("")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME],DeliveryFormViewModel.EMPTY_ITEM_NAME)
    }

    @Test
    fun `set valid itemname on validation when itemname is valid`(){
        viewModel.validateItemName("Box")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME],DeliveryFormViewModel.VAL_VALID)
    }

    @Test
    fun `set empty pickUpAddress error on validation when pickUpAddress is empty`(){
        viewModel.validatePickUpAddress("")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_ADDRESS]
            ,DeliveryFormViewModel.EMPTY_PICK_UP_ADDRESS)
    }

    @Test
    fun `set valid pickUpAddress on validation when pickUpAddress is valid`(){
        viewModel.validatePickUpAddress("Tema")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_ADDRESS]
            ,DeliveryFormViewModel.VAL_VALID)
    }

    @Test
    fun `set empty destinationAddress error on validation when destinationAddress is empty`(){
        viewModel.validateDestinationAddress("")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_DESTINATION_ADDRESS]
            ,DeliveryFormViewModel.EMPTY_DESTINATION_ADDRESS)
    }

    @Test
    fun `set valid destinationAddress on validation when destinationAddress is valid`(){
        viewModel.validateDestinationAddress("Kumasi")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_DESTINATION_ADDRESS]
            ,DeliveryFormViewModel.VAL_VALID)
    }

    @Test
    fun `set empty pickUpDate error on validation when pickUpDate is empty`(){
        viewModel.validatePickUpDate("")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_DATE]
            ,DeliveryFormViewModel.EMPTY_PICK_UP_DATE)
    }

    @Test
    fun `set valid pickUpDate on validation when pickUpDate is valid`(){
        viewModel.validatePickUpDate("Wednesday Jan 2,2019")
        assertEquals(viewModel.validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_DATE]
            ,DeliveryFormViewModel.VAL_VALID)
    }

    @Test
    fun `set invalid new delivery if delivery if item name is invalid`(){
        viewModel.validateNewDelivery("", "address","somewhere",
            "some date")
        assertFalse(viewModel.isNewDeliveryValid())
    }

    @Test
    fun `set invalid new delivery if delivery if pickup address is invalid`(){
        viewModel.validateNewDelivery("name", "","somewhere",
            "some date")
        assertFalse(viewModel.isNewDeliveryValid())
    }

    @Test
    fun `set invalid new delivery if delivery if destination address is invalid`(){
        viewModel.validateNewDelivery("name", "address","",
            "some date")
        assertFalse(viewModel.isNewDeliveryValid())
    }

    @Test
    fun `set invalid new delivery if delivery if pick up date is invalid`(){
        viewModel.validateNewDelivery("name", "address","somewhere",
            "")
        assertFalse(viewModel.isNewDeliveryValid())
    }

    @Test
    fun `set valid new delivery if delivery is valid`(){
        viewModel.validateNewDelivery("name", "address","somewhere",
            "some date")
        assertTrue(viewModel.isNewDeliveryValid())
    }



    @Test
    fun `get directions calls repo get direction results`(){

        val origin = Location()
        val destination = Location()
        val apiKey = "test"
        viewModel.getDirections(origin,destination,apiKey)

        Mockito.verify(apiService).getDirections(any(Location::class.java),any(Location::class.java), anyString(), com.nhaarman.mockitokotlin2.any())

    }
}