package com.example.deliveryapp

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import dagger.android.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject
import javax.inject.Provider

class AndroidTestApplication : MultiDexApplication(),HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector:DispatchingAndroidInjector<Activity>


    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return  dispatchingAndroidInjector
    }

}