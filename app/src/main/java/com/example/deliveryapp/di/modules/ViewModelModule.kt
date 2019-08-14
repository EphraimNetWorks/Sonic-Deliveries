package com.example.deliveryapp.di.modules

import androidx.lifecycle.ViewModel
import com.example.deliveryapp.di.ViewModelKey
import com.example.deliveryapp.ui.login.LoginViewModel
import com.example.deliveryapp.ui.main.MainViewModel
import com.example.deliveryapp.ui.new_delivery.DeliveryFormViewModel
import com.example.deliveryapp.ui.new_delivery.NewDeliveryViewModel
import com.example.deliveryapp.ui.signup.SignUpViewModel
import com.example.deliveryapp.ui.splash.SplashViewModel
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    internal abstract fun signUpViewModel(signUpViewModel: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewDeliveryViewModel::class)
    internal abstract fun newDeliveryViewModel(newDeliveryViewModel: NewDeliveryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrackDeliveryViewModel::class)
    internal abstract fun trackDeliveryViewModel(trackDeliveryViewModel: TrackDeliveryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun splashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DeliveryFormViewModel::class)
    internal abstract fun deliveryFormViewModel(deliveryFormViewModel: DeliveryFormViewModel): ViewModel
    //Others ViewModels
}