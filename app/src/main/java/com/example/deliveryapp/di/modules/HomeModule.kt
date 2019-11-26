package com.example.deliveryapp.di.modules

import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.ViewModelKey
import com.example.deliveryapp.ui.login.LoginViewModel
import com.example.deliveryapp.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [HomeModule.BindsModule::class])
class HomeModule {


    @Module
    interface BindsModule{

        @Binds
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    }
}