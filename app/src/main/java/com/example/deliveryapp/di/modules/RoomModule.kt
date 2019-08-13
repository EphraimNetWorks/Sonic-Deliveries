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

@Module
class RoomModule {
    private val localDatabase: LocalDatabase? = null

    @Singleton
    @Provides
    internal fun providesRoomDatabase(application: Application): LocalDatabase {
        return Room.databaseBuilder(application, LocalDatabase::class.java, "demo-db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }

    companion object {
        internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //do noting since nothing was changed
            }
        }
    }

    @Singleton
    @Provides
    internal fun providesDeliveryDao(localDatabase: LocalDatabase): DeliveryDao {
        return localDatabase.deliveryDao()
    }

    @Singleton
    @Provides
    internal fun providesUserStoryDao(localDatabase: LocalDatabase): UserDao {
        return localDatabase.userDao()
    }

}

