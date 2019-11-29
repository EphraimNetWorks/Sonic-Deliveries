package com.example.deliveryapp.ui.new_delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import javax.inject.Inject

class NewDeliveryViewModel @Inject constructor(private val deliveryRepo:DeliveryRepository) :ViewModel(){

    val networkState: LiveData<NetworkState>
        get() = deliveryRepo.networkState

    fun submitNewDelivery(newDelivery: Delivery){

        deliveryRepo.submitNewDelivery(newDelivery)

    }

}