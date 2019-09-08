package com.example.deliveryapp.data.local.entities

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class DeliveryTest {

    @Test
    fun `set eta timestamp sets eta date`(){
        val testDelivery = Delivery()

        testDelivery.estimatedTimeOfArrival = 1

        assertEquals(testDelivery.estimatedTimeOfArrivalDate!!.timeStamp, testDelivery.estimatedTimeOfArrival!!)
    }

    @Test
    fun `set pick up timestamp sets pick up date`(){
        val testDelivery = Delivery()

        testDelivery.pickUpTime = 1

        assertEquals(testDelivery.pickUpTimeDate!!.timeStamp, testDelivery.pickUpTime!!)
    }

    @Test
    fun `set delivery timestamp sets delivery date`(){
        val testDelivery = Delivery()

        testDelivery.deliveryTime = 1

        assertEquals(testDelivery.deliveryTimeDate!!.timeStamp, testDelivery.deliveryTime!!)
    }

    @Test
    fun `equals is true if deliveries have same ids`(){
        val testDelivery1 = Delivery().apply {
            id = "1"
            title = "A"
        }
        val testDelivery2 = Delivery().apply {
            id = "1"
            title = "B"
        }

        assertEquals(testDelivery1, testDelivery2)
    }

    @Test
    fun `equals is false if deliveries have different ids`(){
        val testDelivery1 = Delivery().apply {
            id = "1"
            title = "A"
        }
        val testDelivery2 = Delivery().apply {
            id = "2"
            title = "B"
        }

        assertNotEquals(testDelivery1, testDelivery2)
    }
}