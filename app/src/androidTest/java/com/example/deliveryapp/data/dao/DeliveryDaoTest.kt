package com.example.deliveryapp.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.deliveryapp.ui.main.MainViewModel
import com.example.deliveryapp.utils.LiveDataTestUtil
import org.joda.time.DateTime
import org.junit.Rule
import javax.sql.DataSource


@RunWith(AndroidJUnit4::class)
class DeliveryDaoTest {
    private lateinit var deliveryDao: DeliveryDao
    private lateinit var db: LocalDatabase

    @Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    var testDeliveries = listOf(
        Delivery().apply {
            id = "1"
            createdAt = 1565434531000
            updatedAt = 1565434531000
            deliveryStatus = Delivery.STATUS_PLACED},
        Delivery().apply {
            id = "2"
            createdAt = 1565435531000
            updatedAt = 1565435531000
            deliveryStatus = Delivery.STATUS_PLACED},
        Delivery().apply {
            id = "3"
            createdAt = 1565435531000
            updatedAt = 1565436531000
            deliveryStatus = Delivery.STATUS_IN_TRANSIT},
        Delivery().apply {
            id = "4"
            createdAt = 1565435531000
            updatedAt = 1565437531000
            deliveryStatus = Delivery.STATUS_COMPLETED},
        Delivery().apply {
            id = "5"
            createdAt = 1565435531000
            updatedAt = 1565438531000
            deliveryStatus = Delivery.STATUS_COMPLETED},
        Delivery().apply {
            id = "6"
            createdAt = 1565435531000
            updatedAt = 1565439531000
            deliveryStatus = Delivery.STATUS_CANCELLED}
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LocalDatabase::class.java).allowMainThreadQueries().build()
        deliveryDao = db.deliveryDao()

        testDeliveries.forEach { deliveryDao.saveMyDelivery(it) }

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeDeliveryAndReadInList() {
        val delivery = Delivery().apply {
            id = "32"
            title = "Box"
        }
        deliveryDao.saveMyDelivery(delivery)
        val result= LiveDataTestUtil.getValue(deliveryDao.getMyDelivery("32"))

        assertThat(result!!.title, equalTo("Box"))

    }

    @Test
    @Throws(Exception::class)
    fun filterDeliveriesPlaced() {
        val pagedListBuilder =
            LivePagedListBuilder<Int, Delivery>(deliveryDao.getDeliveriesPlaced()!!, 30)
        val resultListLD = pagedListBuilder.build()
        resultListLD.observeForever(object : Observer<PagedList<Delivery>>{
            override fun onChanged(results: PagedList<Delivery>?) {
                if(results!=null){
                    assertThat(results[0]!!.id, equalTo(testDeliveries[0].id))
                    assertThat(results[1]!!.id, equalTo(testDeliveries[1].id))
                    resultListLD.removeObserver(this)
                }
            }
        })
    }

    @Test
    @Throws(Exception::class)
    fun filterDeliveriesInTransit() {
        val pagedListBuilder =
            LivePagedListBuilder<Int, Delivery>(deliveryDao.getDeliveriesInTransit()!!, 30)
        val resultListLD = pagedListBuilder.build()
        resultListLD.observeForever(object : Observer<PagedList<Delivery>>{
            override fun onChanged(results: PagedList<Delivery>?) {
                if(results!=null){
                    assertThat(results[0]!!.id, equalTo(testDeliveries[2].id))
                    resultListLD.removeObserver(this)
                }
            }
        })
    }

    @Test
    @Throws(Exception::class)
    fun filterDeliveriesCompleted() {

            val pagedListBuilder =
                LivePagedListBuilder<Int, Delivery>(deliveryDao.getCompletedDeliveries()!!, 30)
            val resultListLD = pagedListBuilder.build()
            resultListLD.observeForever(object : Observer<PagedList<Delivery>>{
                override fun onChanged(results: PagedList<Delivery>?) {
                    if(results!=null){
                        assertThat(results[0]!!.id, equalTo(testDeliveries[3].id))
                        assertThat(results[1]!!.id, equalTo(testDeliveries[4].id))
                        assertThat(results[2]!!.id, equalTo(testDeliveries[5].id))
                        resultListLD.removeObserver(this)
                    }
                }
            })
    }

    @Test
    @Throws(Exception::class)
    fun cancelDelivery() {
        deliveryDao.cancelDelivery(testDeliveries[0].id,DateTime.now().millis)
        val result= LiveDataTestUtil.getValue(deliveryDao.getMyDelivery(testDeliveries[0].id))
        assertThat(result!!.deliveryStatus, equalTo(Delivery.STATUS_CANCELLED))
    }
}