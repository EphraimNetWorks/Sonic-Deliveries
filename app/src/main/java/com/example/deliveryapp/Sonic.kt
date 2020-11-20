package com.example.deliveryapp


import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import com.example.deliveryapp.utils.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
open class Sonic: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }else{
            Timber.plant(ReleaseTree())
        }
    }

}
