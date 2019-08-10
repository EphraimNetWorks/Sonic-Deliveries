package com.example.deliveryapp.data.local.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User

@Dao
interface UserDao {

    @Delete
    fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun getCurrentUser(): User?

}