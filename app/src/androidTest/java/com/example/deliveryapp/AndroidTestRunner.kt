package com.example.deliveryapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.github.tmurakami.dexopener.DexOpener

class AndroidTestRunner :  AndroidJUnitRunner(){

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        // The DexOpener library allows to make Kotlin classes, properties and functions
        // open during tests in order to mock then with Mockito.
        DexOpener.install(this)
        return super.newApplication(cl, com.example.deliveryapp.AndroidTestApplication::class.java.name, context)
    }
}