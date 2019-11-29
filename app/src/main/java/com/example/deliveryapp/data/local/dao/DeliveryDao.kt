package com.example.deliveryapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.deliveryapp.data.local.entities.Delivery

@Dao
interface DeliveryDao {

    @Delete
    fun deleteMyDelivery(delivery: Delivery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMyDelivery(myDelivery: Delivery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMyDeliveries(vararg myDeliveries: Delivery)

    @Query("SELECT * FROM delivery WHERE id=:deliveryId LIMIT 1")
    fun getMyDelivery(deliveryId:String): LiveData<Delivery>

    @Query("UPDATE delivery SET deliveryStatus="+Delivery.STATUS_CANCELLED+", deliveryTime=:cancelledTime, updatedAt=:cancelledTime WHERE id=:deliveryId")
    fun cancelDelivery(deliveryId:String, cancelledTime:Long)

    @Query("SELECT * FROM delivery WHERE deliveryStatus="+Delivery.STATUS_PLACED+" ORDER BY updatedAt DESC")
    fun getDeliveriesPlaced(): DataSource.Factory<Int, Delivery>?

    @Query("SELECT * FROM delivery WHERE deliveryStatus="+Delivery.STATUS_IN_TRANSIT+" ORDER BY updatedAt DESC")
    fun getDeliveriesInTransit(): DataSource.Factory<Int, Delivery>?

    @Query("SELECT * FROM delivery WHERE deliveryStatus="+Delivery.STATUS_COMPLETED
            +" OR deliveryStatus="+Delivery.STATUS_CANCELLED+" ORDER BY updatedAt DESC")
    fun getCompletedDeliveries(): DataSource.Factory<Int, Delivery>?

}