package com.example.deliveryapp.di.modules

import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.ViewModelKey
import com.example.deliveryapp.ui.login.LoginViewModel
import com.example.deliveryapp.ui.new_delivery.DeliveryFormViewModel
import com.example.deliveryapp.ui.new_delivery.NewDeliveryViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [NewDeliveryModule.BindsModule::class])
class NewDeliveryModule {


    @Module
    interface BindsModule{

        @Binds
        @IntoMap
        @ViewModelKey(DeliveryFormViewModel::class)
        fun deliveryFormViewModel(deliveryFormViewModel: DeliveryFormViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(NewDeliveryViewModel::class)
        fun newDeliveryViewModel(newDeliveryViewModel: NewDeliveryViewModel): ViewModel

    }
}