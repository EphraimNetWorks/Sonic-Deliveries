package com.example.deliveryapp.di

import androidx.test.platform.app.InstrumentationRegistry
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.di.modules.MainModule
import com.example.deliveryapp.di.modules.RoomModule

class TestAppInjector {

    private var testMainModule:TestMainModule? = null

    constructor( testMainModule: TestMainModule){
        this.testMainModule = testMainModule
    }

    fun inject() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as AndroidTestApplication

        DaggerTestAppComponent
            .builder()
            .application(app)
            .roomModule(RoomModule())
            .mainModule(testMainModule?: MainModule())
            .build()
            .inject(app)
    }
}