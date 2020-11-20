package com.example.deliveryapp.ui.main


import androidx.hilt.lifecycle.ViewModelInject
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
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.*
import kotlin.random.Random

@ActivityScoped
class MainViewModel @ViewModelInject constructor(
    private val deliveryRepo: DeliveryRepository,
    private val userRepo: UserRepository) :ViewModel() {

    lateinit var deliveriesPlaced: LiveData<PagedList<Delivery>>

    lateinit var deliveriesInTransit: LiveData<PagedList<Delivery>>

    lateinit var completedDeliveries: LiveData<PagedList<Delivery>>

    lateinit var deliveriesPlacedPair: LiveData<Pair<PagedList<Delivery>?, NetworkState?>>

    lateinit var deliveriesInTransitPair: LiveData<Pair<PagedList<Delivery>?, NetworkState?>>

    lateinit var completedDeliveriesPair: LiveData<Pair<PagedList<Delivery>?, NetworkState?>>

    lateinit var deliveriesPlacedNo: LiveData<String>

    lateinit var deliveriesInTransitNo: LiveData<String>

    lateinit var completedDeliveriesNo: LiveData<String>

    var mostRecentDelivery: LiveData<Delivery> = MutableLiveData()

    lateinit var networkState:LiveData<NetworkState>

    var currentUser : LiveData<User>? = null
    private val viewModelJob  = Job()

    init {
        initializeCurrentUser()
    }

    fun initMyDeliveries() {
        initDeliveriesPlaced()
        initDeliveriesInTransit()
        initCompletedDeliveries()

        initializeTransformations()
    }

    private fun initializeTransformations(){

        val repoNetworkState = deliveryRepo.networkState
        networkState = Transformations.map(repoNetworkState) { it }

        deliveriesPlacedNo = Transformations.map(deliveriesPlaced) { it.size.toString()}
        deliveriesInTransitNo = Transformations.map(deliveriesInTransit) { it.size.toString()}
        completedDeliveriesNo = Transformations.map(completedDeliveries) { it.size.toString()}

        deliveriesPlacedPair = Transformations.map(deliveriesPlaced) { Pair(it,networkState.value)}
        deliveriesInTransitPair = Transformations.map(deliveriesInTransit) { Pair(it,networkState.value)}
        completedDeliveriesPair = Transformations.map(completedDeliveries) {
            Pair(it,networkState.value)
        }

        deliveriesPlacedPair = Transformations.map(repoNetworkState) { Pair(deliveriesPlaced.value,it)}
        deliveriesInTransitPair = Transformations.map(repoNetworkState) { Pair(deliveriesInTransit.value,it)}
        completedDeliveriesPair = Transformations.map(repoNetworkState) {
            Pair(completedDeliveries.value,it)}

        mostRecentDelivery = Transformations.map(deliveriesPlaced) { getMostRecentDelivery()}
        mostRecentDelivery = Transformations.map(deliveriesInTransit) { getMostRecentDelivery()}
        mostRecentDelivery = Transformations.map(completedDeliveries) { getMostRecentDelivery()}
    }

    fun getMostRecentDelivery():Delivery?{

        val mostRecentList= arrayListOf<Delivery>()

        //take only first objects since room already sorts delivery
        if(!deliveriesPlaced.value.isNullOrEmpty()) mostRecentList.add(deliveriesPlaced.value!![0]!!)
        if(!deliveriesInTransit.value.isNullOrEmpty()) mostRecentList.add(deliveriesInTransit.value!![0]!!)
        if(!completedDeliveries.value.isNullOrEmpty()) mostRecentList.add(completedDeliveries.value!![0]!!)

        val sortedList = mostRecentList.sortedWith(compareBy { it.updatedAt })

        return if(sortedList.isNotEmpty()) sortedList[sortedList.size -1] else null
    }

    private fun initDeliveriesPlaced() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder(deliveryRepo.getDeliveriesPlaced(), pageSize)
        deliveriesPlaced = pagedListBuilder.build()

    }

    private fun initDeliveriesInTransit() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder(deliveryRepo.getDeliveriesInTransit(), pageSize)
        deliveriesInTransit = pagedListBuilder.build()

    }

    private fun initCompletedDeliveries() {
        val pagedListBuilder: LivePagedListBuilder<Int, Delivery> =
            LivePagedListBuilder(deliveryRepo.getCompletedDeliveries(), pageSize)
        completedDeliveries = pagedListBuilder.build()

    }

    private fun initializeCurrentUser() {
        currentUser = userRepo.getCurrentUser()
    }

    fun getRandomItemFromList(list: List<String>):String{
        val randomNo = Random.nextInt(0,list.size)
        return list[randomNo]
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun logoutUser() {
        userRepo.logoutUser()
    }

    companion object{
        const val pageSize = 20
    }

}
