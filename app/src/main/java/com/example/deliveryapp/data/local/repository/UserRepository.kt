package com.example.deliveryapp.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.LocalDatabase
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
import java.util.concurrent.Executors
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService:ApiService,
                          private val userDao: UserDao,
                          private val localDatabase: LocalDatabase){

    private var _networkState:MutableLiveData<NetworkState> = MutableLiveData()
    var networkState:LiveData<NetworkState> = _networkState

    fun login(email:String, password:String){
        _networkState.postValue(NetworkState.LOADING)
        apiService.loginUser(email,password, object : ApiCallback<User?>{
            override fun onSuccess(result: User?) {
                execute {
                    userDao.saveUser(result!!)
                }
                _networkState.postValue(NetworkState.LOADED)
                Timber.d("login success")
            }

            override fun onFailed(errMsg: String) {
                _networkState.postValue(NetworkState.error(errMsg))
                Timber.w("login failed with error $errMsg")
            }
        })
    }

    fun signUp(signUpRequest: SignUpRequest){
        _networkState.postValue(NetworkState.LOADING)
        apiService.signUpUser(signUpRequest,object : ApiCallback<User?>{
            override fun onSuccess(result: User?) {
                _networkState.postValue(NetworkState.LOADED)
                Timber.d("sign up user success")
            }

            override fun onFailed(errMsg: String) {
                _networkState.postValue(NetworkState.error(errMsg))
                Timber.w("sign up user failed with error: $errMsg")
            }
        })
    }

    fun getCurrentUser(): LiveData<User>? {
        return userDao.getCurrentUser()
    }

    fun logoutUser() {
        execute { localDatabase.clearAllTables() }
        apiService.logoutUser()
    }

    fun execute (block:()->Unit){
        Executors.newSingleThreadScheduledExecutor().execute(block)
    }
}