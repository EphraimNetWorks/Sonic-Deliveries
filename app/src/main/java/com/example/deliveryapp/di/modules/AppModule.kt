package com.example.deliveryapp.di.modules


import android.app.Application
import androidx.room.Room
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.ApiServiceImpl
import com.example.deliveryapp.utils.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
class AppModule{

    @Singleton
    @Provides
    internal fun providesRoomDatabase(application: Application): LocalDatabase {
        return Room.databaseBuilder(application, LocalDatabase::class.java, "sonic_db")
            .addMigrations(LocalDatabase.MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider{
        return DispatcherProvider()
    }

}

@InstallIn(ApplicationComponent::class)
@Module
abstract class ApiServiceModule{
    @Binds
    abstract fun bindsApiService(apiServiceImpl: ApiServiceImpl):ApiService
}