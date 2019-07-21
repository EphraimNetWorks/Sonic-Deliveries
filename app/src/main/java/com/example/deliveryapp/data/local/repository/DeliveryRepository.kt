package com.example.deliveryapp.data.local.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.utils.DispatcherProvider
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DeliveryRepository(private val apiService:ApiService, private val deliveryDao: DeliveryDao){

    var myDeliveries :MutableLiveData<List<Delivery>> = MutableLiveData()

    private var retryCompletable: Completable? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val networkState = MutableLiveData<NetworkState>()

    fun getMyDeliveries(dispatcherProvider: DispatcherProvider = DispatcherProvider()): DataSource.Factory<Int,Delivery>{

        networkState.postValue(NetworkState.LOADING)
        val scope = CoroutineScope(dispatcherProvider.IO)
        scope.launch {
            loadMyDeliveries()
        }
        return deliveryDao.getMyDeliveries()
    }

    private fun loadMyDeliveries(){
        apiService.loadMyDeliveries(object : ApiCallback<List<Delivery>>{
            override fun onSuccess(result: List<Delivery>) {
                Timber.d("get my deliveries success")
                for(delivery in result){
                    deliveryDao.saveMyDelivery(delivery)
                }
                networkState.postValue(NetworkState.LOADED)
            }

            override fun onFailed(errMsg: String) {
                Timber.w("get my deliveries failed with error: $errMsg")
                setRetry(Action { loadMyDeliveries() })
                networkState.postValue(NetworkState.error(errMsg))
            }
        })
    }

    fun getNetworkState(): MutableLiveData<NetworkState> {
        return networkState
    }

    fun retry() {
        this.compositeDisposable = CompositeDisposable()
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { throwable -> Timber.e(throwable.message) }))
        }
    }

    private fun setRetry(action: Action) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }
}