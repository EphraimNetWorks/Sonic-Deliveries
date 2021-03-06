package com.example.deliveryapp.ui.signup

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.data.remote.request.SignUpRequest
import dagger.hilt.android.scopes.ActivityScoped
import java.util.regex.Pattern
import kotlin.collections.HashMap


@ActivityScoped
class SignUpViewModel @ViewModelInject constructor(private val userRepo: UserRepository) :ViewModel(){
    val networkState: LiveData<NetworkState>
        get() = userRepo.networkState

    var validationMap: HashMap<String, Int> = HashMap()


    var emailPattern:Pattern? = null
    var phonePattern:Pattern? = null

    init {
        phonePattern = Patterns.PHONE
        emailPattern = Patterns.EMAIL_ADDRESS

        initializeValidationMap()
    }

    private fun initializeValidationMap(){

        validationMap = HashMap()
        validationMap[VAL_MAP_EMAIL_KEY] = VAL_DEFAULT
        validationMap[VAL_MAP_PASSWORD_KEY] = VAL_DEFAULT
        validationMap[VAL_MAP_PHONE_KEY] = VAL_DEFAULT
        validationMap[VAL_MAP_NAME_KEY] = VAL_DEFAULT
    }

    fun validateSignUpDetails(name:String, phone:String, email:String, password:String, confirmPassword: String):HashMap<String,Int>{
        validateName(name)
        validatePhone(phone)
        validateEmail(email)
        validatePassword(password,confirmPassword)

        return validationMap
    }

    fun validateName(name:String){
        var validationMessage = VAL_VALID
        if(name.isEmpty()){
            validationMessage = EMPTY_NAME
        }

        validationMap[VAL_MAP_NAME_KEY] = validationMessage


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

    fun validatePhone(phone:String){
        var validationMessage = VAL_VALID
        if(phone.isEmpty()){
            validationMessage = EMPTY_PHONE_NUMBER
        }else if(!phonePattern!!.matcher(phone).matches()){
            validationMessage = INVALID_PHONE_NUMBER
        }

        validationMap[VAL_MAP_PHONE_KEY] = validationMessage

    }

    fun validatePassword(password:String, confirmPassword:String){
        var validationMessage = VAL_VALID
        when {
            password.isEmpty() -> validationMessage = EMPTY_PASSWORD
            password.length<6 -> validationMessage = INVALID_PASSWORD
            password != confirmPassword -> validationMessage = PASSWORDS_DONT_MATCH
        }

        validationMap[VAL_MAP_PASSWORD_KEY] = validationMessage
    }

    fun signUpUser(name: String, phone: String, email: String, password: String){
        userRepo.signUp(SignUpRequest(
            name,
            phone,
            email,
            password
        ))
    }

    companion object{

        @StringRes
        const val EMPTY_NAME = R.string.empty_name_error_message

        @StringRes
        const val INVALID_EMAIL_ADDRESS = R.string.invalid_email_error_message
        @StringRes
        const val EMPTY_EMAIL_ADDRESS = R.string.empty_email_field_error

        @StringRes
        const val INVALID_PHONE_NUMBER = R.string.invalid_phone_error_message
        @StringRes
        const val EMPTY_PHONE_NUMBER = R.string.empty_phone_error_message

        @StringRes
        const val INVALID_PASSWORD = R.string.invalid_password_error_message
        @StringRes
        const val PASSWORDS_DONT_MATCH = R.string.password_dont_match_error_message
        @StringRes
        const val EMPTY_PASSWORD = R.string.empty_password_field_error_message

        const val VAL_VALID = 1
        const val VAL_DEFAULT = 0


        const val VAL_MAP_EMAIL_KEY = "email"
        const val VAL_MAP_PASSWORD_KEY = "password"
        const val VAL_MAP_PHONE_KEY = "phone"
        const val VAL_MAP_NAME_KEY = "name"

    }
}