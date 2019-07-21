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

    @Query("SELECT * FROM delivery ORDER BY createdAt DESC")
    fun getMyDeliveries(): DataSource.Factory<Int, Delivery>

}