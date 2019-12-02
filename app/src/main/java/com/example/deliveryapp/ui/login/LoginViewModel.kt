package com.example.deliveryapp.ui.login

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.HashMap


class LoginViewModel @Inject constructor(private val userRepo:UserRepository) :ViewModel(){

    val networkState:LiveData<NetworkState>
        get() = userRepo.networkState

    var validationMap: HashMap<String,Int> = HashMap()
    private val viewModelJob  = Job()

    var emailPattern:Pattern? = null

    init {
        emailPattern = Patterns.EMAIL_ADDRESS

        initializeValidationMap()
    }

    private fun initializeValidationMap(){
        validationMap = hashMapOf(Pair(VAL_MAP_EMAIL_KEY, VAL_DEFAULT),
            Pair(VAL_MAP_PASSWORD_KEY, VAL_DEFAULT))

    }

    fun validateLoginDetails(email:String, password:String):HashMap<String,Int>{
        validateEmail(email)
        validatePassword(password)

        return  validationMap
    }

    fun validateEmail(email:String){
        var validationMessage = VAL_VALID
        if(email.isEmpty()){
            validationMessage = EMPTY_EMAIL_ADDRESS
        }else if(!emailPattern!!.matcher(email).matches()){
            validationMessage = INVALID_EMAIL_ADDRESS
        }

        validationMap[VAL_MAP_EMAIL_KEY] = validationMessage

    }

    fun validatePassword(password:String){
        var validationMessage = VAL_VALID
        if(password.isEmpty()){
            validationMessage = EMPTY_PASSWORD
        }else if(password.length<6){
            validationMessage = INVALID_PASSWORD
        }
        Timber.d("Password:$password valmsg:$validationMessage")
        validationMap[VAL_MAP_PASSWORD_KEY] = validationMessage

    }

    fun loginUser(email: String, password: String){
        userRepo.login(email, password)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object{


        @StringRes
        const val INVALID_EMAIL_ADDRESS = R.string.invalid_email_error_message
        @StringRes
        const val EMPTY_EMAIL_ADDRESS = R.string.empty_email_field_error
        @StringRes
        const val INVALID_PASSWORD = R.string.invalid_password_error_message
        @StringRes
        const val EMPTY_PASSWORD = R.string.empty_password_field_error_message


        const val VAL_VALID = 1
        const val VAL_DEFAULT = 0

        const val VAL_MAP_EMAIL_KEY = "email"
        const val VAL_MAP_PASSWORD_KEY = "password"

    }
}