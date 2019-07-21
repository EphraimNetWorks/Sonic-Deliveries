package com.example.deliveryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.utils.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject


class LoginViewModel @Inject constructor(private val userRepo:UserRepository,
                                         private val dispatcherProvider: DispatcherProvider = DispatcherProvider()
) :ViewModel(){

    private lateinit var networkState:LiveData<NetworkState>
    var validationMap: MutableLiveData<WeakHashMap<String,String>> = MutableLiveData()
    var currentUser :User? = null
    private val viewModelJob  = Job()

    init {
        initializeCurrentUser()
        initializeValidationMap()
    }

    private fun initializeValidationMap(){
        val initialMap = WeakHashMap<String,String>()
        initialMap[VAL_MAP_EMAIL_KEY] = ""
        initialMap[VAL_MAP_PASSWORD_KEY] = ""
        validationMap.value = initialMap
    }

    private fun initializeCurrentUser(){
        GlobalScope.launch(dispatcherProvider.Main + viewModelJob) {
            currentUser = userRepo.getCurrentUser()
        }
    }

    fun validateLoginDetails(email:String, password:String){
        validateEmail(email)
        validatePassword(password)
    }

    fun validateEmail(email:String){
        var validationMessage = VAL_VALID
        if(email.isEmpty()){
            validationMessage = EMPTY_EMAIL_ADDRESS
        }else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
            validationMessage = INVALID_EMAIL_ADDRESS
        }
        val map = validationMap.value!!
        map[VAL_MAP_EMAIL_KEY] = validationMessage
        validationMap.value = map

    }

    fun validatePassword(password:String){
        var validationMessage = VAL_VALID
        if(password.isEmpty()){
            validationMessage = EMPTY_PASSWORD
        }else if(password.length<6){
            validationMessage = INVALID_PASSWORD
        }
        val map = validationMap.value!!
        map[VAL_MAP_PASSWORD_KEY] = validationMessage
        validationMap.value = map
    }

    fun loginUser(email: String, password: String){
        userRepo.login(email, password)
    }

    fun getNetworkState(): LiveData<NetworkState>{
        this.networkState = userRepo.getNetworkState()
        return networkState
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{
        const val INVALID_EMAIL_ADDRESS = "Invalid Email"
        const val EMPTY_EMAIL_ADDRESS = "Empty Email"
        const val INVALID_PASSWORD = "Invalid password"
        const val EMPTY_PASSWORD = "Empty password"
        const val VAL_VALID = "valid"

        const val VAL_MAP_EMAIL_KEY = "email"
        const val VAL_MAP_PASSWORD_KEY = "password"

        private const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        private val EMAIL_ADDRESS_PATTERN = Pattern.compile(EMAIL_PATTERN)

    }
}