package com.example.deliveryapp.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.example.deliveryapp.data.remote.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

open class UserRepository(private val apiService:ApiService, private val userDao: UserDao){

    private var networkState:MutableLiveData<NetworkState> = MutableLiveData()

    fun getNetworkState():LiveData<NetworkState>{
        return networkState
    }

    fun login(email:String, password:String){
        networkState.postValue(NetworkState.LOADING)
        apiService.loginUser(email,password, object : ApiCallback<User?>{
            override fun onSuccess(result: User?) {
                Thread {
                    userDao.saveUser(result!!)
                }.start()
                networkState.postValue(NetworkState.LOADED)
                Timber.d("login success")
            }

            override fun onFailed(errMsg: String) {
                networkState.postValue(NetworkState.error(errMsg))
                Timber.w("login failed with error $errMsg")
            }
        })
    }

    fun signUp(signUpRequest: SignUpRequest){
        networkState.postValue(NetworkState.LOADING)
        apiService.signUpUser(signUpRequest,object : ApiCallback<User?>{
            override fun onSuccess(result: User?) {
                networkState.postValue(NetworkState.LOADED)
                Timber.d("sign up user success")
            }

            override fun onFailed(errMsg: String) {
                networkState.postValue(NetworkState.error(errMsg))
                Timber.w("sign up user failed with error: $errMsg")
            }
        })
    }

    fun getCurrentUser(): LiveData<User>? {
        return userDao.getCurrentUser()
    }
}