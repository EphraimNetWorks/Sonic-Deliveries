package com.example.deliveryapp.ui.main

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.utils.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val deliveryRepo: DeliveryRepository,
    private val userRepo: UserRepository,
    private val dispatcherProvider: DispatcherProvider = DispatcherProvider()) :ViewModel() {

    var myDeliveries: LiveData<PagedList<Delivery>>? = null
    private var networkState:LiveData<NetworkState>? = null
    var currentUser : User? = null
    private val viewModelJob  = Job()

    init {
        initializeCurrentUser()
    }

    fun initMyDeliveries() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder<Int, Delivery>(deliveryRepo.getMyDeliveries(), pageSize)
        myDeliveries = pagedListBuilder.build()

    }

    fun initializeCurrentUser(){
        GlobalScope.launch(dispatcherProvider.Main + viewModelJob) {
            currentUser = userRepo.getCurrentUser()
        }
    }

    fun getNetworkState():LiveData<NetworkState>{

        this.networkState = deliveryRepo.getNetworkState()
        return networkState!!
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        const val pageSize = 20
    }

}
