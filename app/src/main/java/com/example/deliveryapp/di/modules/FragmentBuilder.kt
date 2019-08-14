package com.example.deliveryapp.di.modules

import com.example.deliveryapp.ui.new_delivery.NewDeliveryFormFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    internal abstract fun newDeliveryFormFragment(): NewDeliveryFormFragment

}