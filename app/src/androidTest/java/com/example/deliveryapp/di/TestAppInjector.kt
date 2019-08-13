package com.example.deliveryapp.di

import androidx.test.platform.app.InstrumentationRegistry
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.modules.MainModule
import com.example.deliveryapp.di.modules.RoomModule
import timber.log.Timber

class TestAppInjector {

    private var testMainModule:TestMainModule? = null
    private var userRepository:UserRepository? = null
    private var deliveryRepository: DeliveryRepository? = null
    var component:TestAppComponent?=null

    constructor( testMainModule: TestMainModule){
        this.testMainModule = testMainModule
    }
    constructor(userRepository:UserRepository, deliveryRepository: DeliveryRepository){
        this.userRepository = userRepository
        this.deliveryRepository = deliveryRepository
    }

    fun inject() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as AndroidTestApplication


//        component = DaggerTestAppComponent
//            .builder()
//            .application(app)
//            .roomModule(RoomModule())
//            .testMainModule(testMainModule!!)
//            .build()
//
//        component!!.inject(app)
    }

    fun newInject() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as AndroidTestApplication

        component = DaggerTestAppComponent
            .builder()
            .application(app)
            .roomModule(RoomModule())
            .fakeUserRepo(userRepository!!)
            .fakeDeliveryRepo(deliveryRepository!!)
            .build()

        component!!.inject(app)
    }
}