package com.example.deliveryapp.di

import android.app.Application
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.di.modules.RoomModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import com.example.deliveryapp.di.modules.ActivityBuilder
import com.example.deliveryapp.di.modules.MainTestModule
import com.example.deliveryapp.di.modules.RoomTestModule
import com.example.deliveryapp.ui.delivery.NewDeliveryActivityTest
import com.example.deliveryapp.ui.delivery.TrackDeliveryActivityTest
import com.example.deliveryapp.ui.main.MainActivityTest
import com.example.deliveryapp.ui.user.LoginActivityTest
import com.example.deliveryapp.ui.user.SignUpActivityTest
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ActivityBuilder::class,
    ActivityBuilder::class, MainTestModule::class, RoomTestModule::class])
interface TestAppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun mainTestModule(mainTestModule: MainTestModule): Builder

        @BindsInstance
        fun roomTestModule(roomTestModule: RoomTestModule): Builder

        fun build(): TestAppComponent

    }

    fun inject(testRunner: AndroidTestApplication)
    fun inject(newDeliveryActivityTest: NewDeliveryActivityTest)
    fun inject(mainActivityTest: MainActivityTest)
    fun inject(loginActivityTest: LoginActivityTest)
    fun inject(signUpActivityTest: SignUpActivityTest)
}