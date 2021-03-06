package com.example.deliveryapp.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.Location
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.utils.DispatcherProvider
import com.google.maps.model.DirectionsResult
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject


class DeliveryRepository @Inject constructor(private val apiService:ApiService, private val deliveryDao: DeliveryDao){

    private var retryCompletable: Completable? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState:LiveData<NetworkState>
        get() = _networkState

    fun getDeliveriesPlaced(dispatcherProvider: DispatcherProvider = DispatcherProvider()): DataSource.Factory<Int,Delivery>{

        _networkState.postValue(NetworkState.LOADING)
        val scope = CoroutineScope(dispatcherProvider.IO)
        scope.launch {
            loadMyDeliveries()
        }
        return deliveryDao.getDeliveriesPlaced()!!
    }

    fun getDeliveriesInTransit(dispatcherProvider: DispatcherProvider = DispatcherProvider()): DataSource.Factory<Int,Delivery>{

        _networkState.postValue(NetworkState.LOADING)
        val scope = CoroutineScope(dispatcherProvider.IO)
        scope.launch {
            loadMyDeliveries()
        }
        return deliveryDao.getDeliveriesInTransit()!!
    }

    fun getCompletedDeliveries(dispatcherProvider: DispatcherProvider = DispatcherProvider()): DataSource.Factory<Int,Delivery>{

        _networkState.postValue(NetworkState.LOADING)
        val scope = CoroutineScope(dispatcherProvider.IO)
        scope.launch {
            loadMyDeliveries()
        }
        return deliveryDao.getCompletedDeliveries()!!
    }

    private fun loadMyDeliveries(){
        apiService.loadMyDeliveries(object : ApiCallback<List<Delivery>>{
            override fun onSuccess(result: List<Delivery>) {
                Timber.d("get my deliveries success")
                execute{
                    deliveryDao.saveMyDeliveries(*result.toTypedArray())
                }
                _networkState.postValue(NetworkState.LOADED)
            }

            override fun onFailed(errMsg: String) {
                Timber.w("get my deliveries failed with error: $errMsg")
                setRetry(Action { loadMyDeliveries() })
                _networkState.postValue(NetworkState.error(errMsg))
            }
        })
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

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

    fun cancelDelivery(deliveryId: String) {
        _networkState.postValue(NetworkState.LOADING)

        apiService.cancelDelivery(deliveryId, object : ApiCallback<Boolean>{
            override fun onSuccess(result: Boolean) {
                execute{
                    deliveryDao.cancelDelivery(deliveryId,DateTime.now().millis)
                }
                _networkState.postValue(NetworkState.LOADED)
            }

            override fun onFailed(errMsg: String) {
                _networkState.postValue(NetworkState.error(errMsg))
            }
        })
    }

    fun submitNewDelivery(newDelivery: Delivery) {
        _networkState.postValue(NetworkState.LOADING)

        apiService.sendNewDelivery(newDelivery, object : ApiCallback<Boolean>{
            override fun onSuccess(result: Boolean) {
                execute{
                    deliveryDao.saveMyDelivery(newDelivery)
                }
                _networkState.postValue(NetworkState.LOADED)
            }

            override fun onFailed(errMsg: String) {
                _networkState.postValue(NetworkState.error(errMsg))
            }
        })
    }

    val directionResults = MutableLiveData<DirectionsResult>()

    fun getDirectionResults(origin: Location, destination: Location, apiKey: String): LiveData<DirectionsResult> {
        _networkState.postValue(NetworkState.LOADING)

        apiService.getDirections(origin,destination,apiKey, object : ApiCallback<DirectionsResult>{
            override fun onSuccess(result: DirectionsResult) {

                directionResults.postValue(result)
                Timber.d("get direction result success")
            }

            override fun onFailed(errMsg: String) {
                Timber.d("get direction result failed with error: $errMsg")
            }
        })
        return directionResults
    }



    fun execute (block:()->Unit){
        Executors.newSingleThreadScheduledExecutor().execute(block)
    }
}