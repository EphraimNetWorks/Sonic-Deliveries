package io.acsint.heritageGhana.MtnHeritageGhanaApp.di

import android.app.Application
import com.example.deliveryapp.Sonic


import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import io.acsint.heritageGhana.MtnHeritageGhanaApp.di.modules.ActivityBuilder
import com.example.deliveryapp.di.modules.MainModule
import com.example.deliveryapp.di.modules.RoomModule
import io.acsint.heritageGhana.MtnHeritageGhanaApp.di.modules.ViewModelModule

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    RoomModule::class,
    ViewModelModule::class,
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

