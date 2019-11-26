package com.example.deliveryapp.di.modules

import android.app.Application

import javax.inject.Singleton

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
class RoomTestModule {

    @Singleton
    @Provides
    internal fun providesRoomDatabase(): LocalDatabase {
        return Mockito.mock(LocalDatabase::class.java)
    }

    @Singleton
    @Provides
    internal fun providesDeliveryDao(localDatabase: LocalDatabase): DeliveryDao {
        return Mockito.mock(DeliveryDao::class.java)
    }

    @Singleton
    @Provides
    internal fun providesUserStoryDao(): UserDao {
        return Mockito.mock(UserDao::class.java)
    }

}

