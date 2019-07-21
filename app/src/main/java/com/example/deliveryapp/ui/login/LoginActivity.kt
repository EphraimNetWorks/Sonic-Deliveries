package com.example.deliveryapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivityLoginBinding
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(LoginViewModel::class.java)

        if(viewModel.currentUser!=null){
            goToNextActivity()
        }

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

    }

    fun validateAndLoginUser(){

        val email = binding.loginEmailEditext.text.toString()
        val password = binding.loginPasswordEditext.text.toString()
        viewModel.validateLoginDetails(email, password)

        viewModel.validationMap.observe(this, Observer { valMap-> processValidationMap(valMap, email, password)})

        viewModel.getNetworkState().observe(this, Observer { networkState->
            binding.networkState = networkState
            handleNetworkState(networkState)})

    }

    private fun handleNetworkState(networkState: NetworkState) {
        if(networkState.status == Status.SUCCESS){
            goToNextActivity()
        }
    }

    private fun goToNextActivity(){

        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun processValidationMap(valMap: WeakHashMap<String,String>, email:String, password:String){
        when {
            valMap[LoginViewModel.VAL_MAP_EMAIL_KEY]!= LoginViewModel.VAL_VALID -> {
                val errorMessage = when(valMap[LoginViewModel.VAL_MAP_EMAIL_KEY]){
                    LoginViewModel.EMPTY_EMAIL_ADDRESS -> getString(R.string.empty_email_field_error)
                    LoginViewModel.INVALID_EMAIL_ADDRESS -> getString(R.string.invalid_email_error_message)
                    else -> throw IllegalArgumentException("Unknown validation error")
                }
                binding.loginEmailTextLayout.error = errorMessage

            }
            valMap[LoginViewModel.VAL_MAP_PASSWORD_KEY] != LoginViewModel.VAL_VALID -> {
                val errorMessage = when(valMap[LoginViewModel.VAL_MAP_PASSWORD_KEY]){
                    LoginViewModel.EMPTY_PASSWORD -> getString(R.string.empty_password_field_error_message)
                    LoginViewModel.INVALID_PASSWORD -> getString(R.string.invalid_password_error_message)
                    else -> throw IllegalArgumentException("Unknown validation error")
                }
                binding.loginPasswordTextLayout.error = errorMessage

            }
            else -> viewModel.loginUser(email,password)
        }
    }

    override fun onDestroy() {
        viewModel.validationMap.removeObservers(this)
        viewModel.getNetworkState().removeObservers(this)
        super.onDestroy()
    }
}