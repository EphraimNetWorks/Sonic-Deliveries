package com.example.deliveryapp.di.modules


import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.ui.splash.SplashActivity
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {


    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindSignUpActivity(): SignUpActivity

    @ContributesAndroidInjector(modules = [FragmentBuilder::class])
    internal abstract fun bindNewDeliveryActivity(): NewDeliveryActivity

    @ContributesAndroidInjector
    internal abstract fun bindTrackDeliveryActivity(): TrackDeliveryActivity

    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity

}
