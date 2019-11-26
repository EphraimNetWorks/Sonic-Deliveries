package com.example.deliveryapp.di.modules


import com.example.deliveryapp.di.scopes.ActivityScope
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


    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeModule::class, DeliveryModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [LoginModule::class, DeliveryModule::class])
    internal abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignUpModule::class])
    internal abstract fun bindSignUpActivity(): SignUpActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [NewDeliveryModule::class, DeliveryModule::class, FragmentBuilder::class])
    internal abstract fun bindNewDeliveryActivity(): NewDeliveryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [TrackDeliveryModule::class, DeliveryModule::class])
    internal abstract fun bindTrackDeliveryActivity(): TrackDeliveryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SplashModule::class])
    internal abstract fun bindSplashActivity(): SplashActivity

}
