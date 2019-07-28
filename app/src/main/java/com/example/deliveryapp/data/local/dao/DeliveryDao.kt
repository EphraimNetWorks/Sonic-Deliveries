package com.example.deliveryapp.data.local.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.deliveryapp.data.local.entities.Delivery

@Dao
interface DeliveryDao {

    @Delete
    fun deleteMyDelivery(delivery: Delivery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMyDelivery(myDelivery: Delivery)

    @Query("SELECT * FROM delivery WHERE deliveryStatus=:status ORDER BY createdAt DESC")
    fun getDeliveriesPlaced(status: Int = Delivery.STATUS_PLACED): DataSource.Factory<Int, Delivery>

    @Query("SELECT * FROM delivery WHERE deliveryStatus=:status ORDER BY createdAt DESC")
    fun getDeliveriesInTransit(status: Int = Delivery.STATUS_IN_TRANSIT): DataSource.Factory<Int, Delivery>

    @Query("SELECT * FROM delivery WHERE deliveryStatus=:status OR deliveryStatus=:status2 ORDER BY createdAt DESC")
    fun getCompletedDeliveries(status: Int = Delivery.STATUS_COMPLETED,
                               status2:Int = Delivery.STATUS_CANCELLED): DataSource.Factory<Int, Delivery>

    @Query("UPDATE delivery SET deliveryStatus=:status WHERE id =:deliveryId ")
    fun updateDeliveryStatus(deliveryId:String, status: Int)

}