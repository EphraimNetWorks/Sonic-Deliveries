package com.example.deliveryapp.di

import androidx.test.platform.app.InstrumentationRegistry
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.di.modules.MainTestModule
import com.example.deliveryapp.di.modules.RoomTestModule
import timber.log.Timber
import kotlin.reflect.KClass

object TestAppInjector {


    fun inject(injectBlock:(tComponent: TestAppComponent)->Unit) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as AndroidTestApplication

        val component = DaggerTestAppComponent
            .builder()
            .mainTestModule(MainTestModule())
            .roomTestModule(RoomTestModule())
            .application(app).build()

        component.inject(app)

        injectBlock.invoke(component)
    }
}