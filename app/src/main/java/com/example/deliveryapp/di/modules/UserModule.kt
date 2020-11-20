package com.example.deliveryapp.di.modules

import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class UserModule {


    @Provides
    internal fun providesUserDao(localDatabase: LocalDatabase): UserDao {
        return localDatabase.userDao()
    }


}