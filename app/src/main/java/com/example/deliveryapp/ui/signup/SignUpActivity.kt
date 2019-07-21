package com.example.deliveryapp.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivitySignUpBinding
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel:SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(SignUpViewModel::class.java)
    }

    fun validateAndSignUpUser(){
        viewModel.validateSignUpDetails(binding.signupNameEditext.text.toString(),
            binding.signupPhoneEditext.text.toString(),
            binding.signupEmailEditext.text.toString(),
            binding.signupPasswordEditext.text.toString(),
            binding.signupConfirmPasswordEditext.text.toString())

        viewModel.validationMap.observe(this, Observer { valMap-> processValidationMap(valMap)})

        viewModel.getNetworkState().observe(this, Observer { networkState->
            binding.networkState = networkState
            goToNextActivity()
        })

    }

    private fun handleNetworkState(networkState: NetworkState) {
        if(networkState.status == Status.SUCCESS){
            goToNextActivity()
        }
    }

    private fun goToNextActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun processValidationMap(valMap: WeakHashMap<String, String>){
        if(valMap[SignUpViewModel.VAL_MAP_NAME_KEY]!= SignUpViewModel.VAL_VALID){
            val errorMessage = when(valMap[SignUpViewModel.VAL_MAP_NAME_KEY]){
                SignUpViewModel.EMPTY_NAME -> getString(R.string.empty_name_error_message)
                else -> throw IllegalArgumentException("Unknown validation error")
            }
            binding.signupNameTextLayout.error = errorMessage

        }else if(valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!= SignUpViewModel.VAL_VALID){
            val errorMessage = when(valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]){
                SignUpViewModel.EMPTY_PHONE_NUMBER -> getString(R.string.empty_phone_error_message)
                SignUpViewModel.INVALID_PHONE_NUMBER -> getString(R.string.invalid_phone_error_message)
                else -> throw IllegalArgumentException("Unknown validation error")
            }
            binding.signupPhoneTextLayout.error = errorMessage

        }else if(valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!= SignUpViewModel.VAL_VALID){
            val errorMessage = when(valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]){
                SignUpViewModel.EMPTY_EMAIL_ADDRESS -> getString(R.string.empty_email_field_error)
                SignUpViewModel.INVALID_EMAIL_ADDRESS -> getString(R.string.invalid_email_error_message)
                else -> throw IllegalArgumentException("Unknown validation error")
            }
            binding.signupEmailTextLayout.error = errorMessage

        }else if (valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] != SignUpViewModel.VAL_VALID){
            val errorMessage = when(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]){
                SignUpViewModel.EMPTY_PASSWORD -> getString(R.string.empty_password_field_error_message)
                SignUpViewModel.INVALID_PASSWORD -> getString(R.string.invalid_password_error_message)
                SignUpViewModel.PASSWORDS_DONT_MATCH -> getString(R.string.password_dont_match_error_message)
                else -> throw IllegalArgumentException("Unknown validation error")
            }
            binding.signupPasswordTextLayout.error = errorMessage
            if(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] == SignUpViewModel.PASSWORDS_DONT_MATCH){
                binding.signupConfirmPasswordTextLayout.error = errorMessage
            }

        }else{
            viewModel.signUpUser(binding.signupNameEditext.text.toString(),
                binding.signupPhoneEditext.text.toString(),
                binding.signupEmailEditext.text.toString(),
                binding.signupPasswordEditext.text.toString())
        }
    }


    override fun onDestroy() {
        viewModel.validationMap.removeObservers(this)
        viewModel.getNetworkState().removeObservers(this)
        super.onDestroy()
    }
}
