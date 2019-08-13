package com.example.deliveryapp

import android.app.Activity
import android.app.Service
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.example.deliveryapp.di.AppInjector
import com.example.deliveryapp.utils.ReleaseTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import timber.log.Timber
import javax.inject.Inject

class Sonic: MultiDexApplication() , HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }else{
            Timber.plant(ReleaseTree())
        }
    }



    override fun activityInjector(): AndroidInjector<Activity> {
        return  dispatchingAndroidInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return serviceDispatchingAndroidInjector
    }
}
