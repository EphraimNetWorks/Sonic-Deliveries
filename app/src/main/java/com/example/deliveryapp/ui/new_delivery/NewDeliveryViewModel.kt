package com.example.deliveryapp.ui.new_delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.ui.signup.SignUpViewModel
import java.util.*
import javax.inject.Inject

class NewDeliveryViewModel @Inject constructor(private val deliveryRepo:DeliveryRepository) :ViewModel(){

    var networkState: LiveData<NetworkState>? = null

    init {
        val nState = deliveryRepo.getNetworkState()
        networkState = Transformations.map(nState){state-> state}
    }

    fun submitNewDelivery(newDelivery: Delivery){

        deliveryRepo.submitNewDelivery(newDelivery)

    }

}