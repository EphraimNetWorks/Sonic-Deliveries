package com.example.deliveryapp.di

import android.app.Application
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.di.modules.MainModule
import com.example.deliveryapp.di.modules.RoomModule
import com.example.deliveryapp.ui.user.LoginActivityTest
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import io.acsint.heritageGhana.MtnHeritageGhanaApp.di.modules.ActivityBuilder
import io.acsint.heritageGhana.MtnHeritageGhanaApp.di.modules.ViewModelModule
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,ActivityBuilder::class,
        RoomModule::class,
    ViewModelModule::class,
    ActivityBuilder::class, TestMainModule::class])
interface TestAppComponent :AppComponent{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun roomModule(roomModule: RoomModule): Builder

        @BindsInstance
        fun fakeUserRepo(fakeUserRepo: UserRepository): Builder

        @BindsInstance
        fun fakeDeliveryRepo(fakeDeliveryRepo: DeliveryRepository): Builder

        fun build(): TestAppComponent
    }

    fun inject(testRunner: AndroidTestApplication)
    fun inject(test: LoginActivityTest)
}