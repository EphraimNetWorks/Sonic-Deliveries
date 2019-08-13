package com.example.deliveryapp.ui.track_delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import javax.inject.Inject

class TrackDeliveryViewModel @Inject constructor(private val deliveryRepo: DeliveryRepository) : ViewModel(){

    private lateinit var networkStateLD: LiveData<NetworkState>

    fun cancelDelivery(delivery:Delivery){
        deliveryRepo.cancelDelivery(delivery.id)
    }

    fun getNetWorkState():LiveData<NetworkState>{
        this.networkStateLD = deliveryRepo.getNetworkState()
        return networkStateLD
    }
}