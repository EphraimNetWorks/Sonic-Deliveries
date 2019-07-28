package com.example.deliveryapp.ui.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.data.remote.request.SignUpRequest
import timber.log.Timber
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpViewModel @Inject constructor(private val userRepo: UserRepository) :ViewModel(){
    private lateinit var networkState: LiveData<NetworkState>
    var validationMap: MutableLiveData<WeakHashMap<String, String>> = MutableLiveData()

    init {
        initializeValidationMap()
    }

    private fun initializeValidationMap(){
        val initialMap = WeakHashMap<String,String>()
        initialMap[VAL_MAP_EMAIL_KEY] = ""
        initialMap[VAL_MAP_PASSWORD_KEY] = ""
        initialMap[VAL_MAP_PHONE_KEY] = ""
        initialMap[VAL_MAP_NAME_KEY] = ""
        validationMap.value = initialMap
    }

    fun validateSignUpDetails(name:String, phone:String, email:String, password:String, confirmPassword: String){
        validateName(name)
        validatePhone(phone)
        validateEmail(email)
        validatePassword(password,confirmPassword)
    }

    fun validateName(name:String){
        var validationMessage = VAL_VALID
        if(name.isEmpty()){
            validationMessage = EMPTY_NAME
        }
        val map = validationMap.value!!
        map[VAL_MAP_NAME_KEY] = validationMessage
        validationMap.value = map

    }

    fun validateEmail(email:String, emailPattern: Pattern = Patterns.EMAIL_ADDRESS){
        var validationMessage = VAL_VALID
        if(email.isEmpty()){
            validationMessage = EMPTY_EMAIL_ADDRESS
        }else if(!emailPattern.matcher(email).matches()){
            validationMessage = INVALID_EMAIL_ADDRESS
        }
        val map = validationMap.value!!
        map[VAL_MAP_EMAIL_KEY] = validationMessage
        validationMap.value = map

    }

    fun validatePhone(phone:String,phonePattern: Pattern = Patterns.PHONE){
        var validationMessage = VAL_VALID
        if(phone.isEmpty()){
            validationMessage = EMPTY_PHONE_NUMBER
        }else if(!phonePattern.matcher(phone).matches()){
            validationMessage = INVALID_PHONE_NUMBER
        }
        val map = validationMap.value!!
        map[VAL_MAP_PHONE_KEY] = validationMessage
        validationMap.value = map

    }

    fun validatePassword(password:String, confirmPassword:String){
        var validationMessage = VAL_VALID
        if(password.isEmpty()){
            validationMessage = EMPTY_PASSWORD
        }else if(password.length<6){
            validationMessage = INVALID_PASSWORD
        }else if(password != confirmPassword){
            validationMessage = PASSWORDS_DONT_MATCH
        }
        val map = validationMap.value!!
        map[VAL_MAP_PASSWORD_KEY] = validationMessage
        validationMap.value = map
    }

    fun signUpUser(name: String, phone: String, email: String, password: String){
        userRepo.signUp(SignUpRequest(
            name,
            phone,
            email,
            password
        ))
    }

    fun getNetworkState(): LiveData<NetworkState> {
        this.networkState = userRepo.getNetworkState()
        return networkState
    }

    companion object{

        const val EMPTY_NAME = "Empty name"

        const val INVALID_EMAIL_ADDRESS = "Invalid Email"
        const val EMPTY_EMAIL_ADDRESS = "Empty Email"

        const val INVALID_PHONE_NUMBER = "Invalid phone number"
        const val EMPTY_PHONE_NUMBER = "Empty phone number"

        const val INVALID_PASSWORD = "Invalid password"
        const val PASSWORDS_DONT_MATCH = "Passwords don't match"
        const val EMPTY_PASSWORD = "Empty password"

        const val VAL_VALID = "valid"


        const val VAL_MAP_EMAIL_KEY = "email"
        const val VAL_MAP_PASSWORD_KEY = "password"
        const val VAL_MAP_PHONE_KEY = "phone"
        const val VAL_MAP_NAME_KEY = "name"

    }
}