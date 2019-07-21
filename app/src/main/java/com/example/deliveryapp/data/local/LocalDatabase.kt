package com.example.deliveryapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User


@Database(entities = [User::class,Delivery::class], version = 1, exportSchema = false)
//@androidx.room.TypeConverters(SonicTypeConverter::class)
abstract class LocalDatabase : RoomDatabase(){

    abstract fun deliveryDao() : DeliveryDao

    abstract fun userDao() : UserDao

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //database.execSQL("ALTER TABLE users ADD COLUMN last_update INTEGER")
        }
    }
}

