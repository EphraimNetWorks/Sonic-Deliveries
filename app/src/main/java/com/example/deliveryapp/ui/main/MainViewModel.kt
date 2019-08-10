package com.example.deliveryapp.ui.main

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.google.gson.Gson
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class MainViewModel @Inject constructor(
    private val deliveryRepo: DeliveryRepository,
    private val userRepo: UserRepository,
    private val dispatcherProvider: DispatcherProvider = DispatcherProvider()) :ViewModel() {

    var deliveriesPlaced: LiveData<PagedList<Delivery>>? = null

    var deliveriesInTransit: LiveData<PagedList<Delivery>>? = null

    var completedDeliveries: LiveData<PagedList<Delivery>>? = null

    var mostRecentDelivery: LiveData<Delivery> = MutableLiveData()

    private var networkState:LiveData<NetworkState>? = null
    var currentUser : User? = null
    private val viewModelJob  = Job()

    init {
        initializeCurrentUser()

    }

    fun initMyDeliveries() {
        initDeliveriesPlaced()
        initDeliveriesInTransit()
        initCompletedDeliveries()

        mostRecentDelivery = Transformations.map(deliveriesPlaced!!) { getMostRecentDelivery()}
        mostRecentDelivery = Transformations.map(deliveriesInTransit!!) { getMostRecentDelivery()}
        mostRecentDelivery = Transformations.map(completedDeliveries!!) { getMostRecentDelivery()}
    }

    fun getMostRecentDelivery():Delivery{

        val mostRecentList: ArrayList<Delivery> = ArrayList()
        if(!deliveriesPlaced!!.value.isNullOrEmpty()) mostRecentList.add(deliveriesPlaced!!.value!![0]!!)
        if(!deliveriesInTransit!!.value.isNullOrEmpty()) mostRecentList.add(deliveriesInTransit!!.value!![0]!!)
        if(!completedDeliveries!!.value.isNullOrEmpty()) mostRecentList.add(completedDeliveries!!.value!![0]!!)

        val sortedList = mostRecentList.sortedWith(compareBy { it.updatedAt })

        return sortedList[sortedList.size -1]
    }

    fun initDeliveriesPlaced() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder<Int, Delivery>(deliveryRepo.getDeliveriesPlaced(), pageSize)
        deliveriesPlaced = pagedListBuilder.build()

    }

    fun initDeliveriesInTransit() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder<Int, Delivery>(deliveryRepo.getDeliveriesInTransit(), pageSize)
        deliveriesInTransit = pagedListBuilder.build()

    }

    fun initCompletedDeliveries() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder<Int, Delivery>(deliveryRepo.getCompletedDeliveries(), pageSize)
        completedDeliveries = pagedListBuilder.build()

    }

    private fun initializeCurrentUser() = runBlocking{
        currentUser = userRepo.getCurrentUser()
    }

    fun getNetworkState():LiveData<NetworkState>{

        this.networkState = deliveryRepo.getNetworkState()
        return networkState!!
    }

    fun getRandomItemFromList(list: List<String>):String{
        val randomNo = Random.nextInt(0,list.size)
        return list[randomNo]
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        const val pageSize = 20
    }

}
