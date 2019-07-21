package com.example.deliveryapp

import androidx.multidex.MultiDexApplication
import com.example.deliveryapp.di.AppInjector

class Sonic: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

}
