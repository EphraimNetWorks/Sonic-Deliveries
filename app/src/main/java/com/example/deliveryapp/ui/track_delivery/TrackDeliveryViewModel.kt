package com.example.deliveryapp.ui.track_delivery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import dagger.hilt.android.scopes.ActivityScoped



@ActivityScoped
class TrackDeliveryViewModel @ViewModelInject constructor(private val deliveryRepo: DeliveryRepository) : ViewModel(){

    val networkState: LiveData<NetworkState>
        get() = deliveryRepo.networkState

    fun cancelDelivery(delivery:Delivery){
        deliveryRepo.cancelDelivery(delivery.id)
    }

}