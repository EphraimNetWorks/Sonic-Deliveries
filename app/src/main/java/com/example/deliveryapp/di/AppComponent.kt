package com.example.deliveryapp.di

import android.app.Application
import com.example.deliveryapp.Sonic
import com.example.deliveryapp.di.modules.*


import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    RoomModule::class,
    ActivityBuilder::class,
    MainModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun roomModule(roomModule: RoomModule): Builder

        @BindsInstance
        fun mainModule(mainModule: MainModule): Builder

        fun build(): AppComponent
    }

    fun inject(sonic: Sonic)
}

