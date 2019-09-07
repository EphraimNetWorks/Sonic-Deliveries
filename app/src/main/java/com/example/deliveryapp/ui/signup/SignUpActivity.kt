package com.example.deliveryapp.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.deliveryapp.R
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivitySignUpBinding
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.utils.EspressoTestingIdlingResource
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class SignUpActivity : AppCompatActivity(),Injectable {

    private lateinit var binding: ActivitySignUpBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel:SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(SignUpViewModel::class.java)

        binding.signupButton.setOnClickListener {
            val name = binding.signupNameEditext.text.toString()
            val phone = binding.signupPhoneEditext.text.toString()
            val email = binding.signupEmailEditext.text.toString()
            val password = binding.signupPasswordEditext.text.toString()
            val confirmPassword = binding.signupConfirmPasswordEditext.text.toString()

            Timber.d("$name, $phone, $email, $password, $confirmPassword")
            validateAndSignUpUser(name, phone, email, password, confirmPassword)
        }
    }

    private fun validateAndSignUpUser(name:String, phone:String, email:String, password:String, confirmPassword: String){

        val valMap = viewModel.validateSignUpDetails(name, phone, email, password, confirmPassword)

        processValidationMap(valMap)

    }

    private fun handleNetworkState(networkState: NetworkState) {
        if(networkState.status == Status.SUCCESS){
            goToNextActivity()
            EspressoTestingIdlingResource.decrement()
        }else if(networkState.status == Status.FAILED){
            binding.signupButton.visibility = View.VISIBLE
            EspressoTestingIdlingResource.decrement()
        }
    }

    private fun goToNextActivity(){
        startActivity(LoginActivity.newInstance(this,MainActivity.SALUTATION_TYPE_SIGN_UP))
    }

    private fun processValidationMap(valMap: HashMap<String, Int>){
        resetTextInputLayoutErrors()

        if(valMap[SignUpViewModel.VAL_MAP_NAME_KEY]!= SignUpViewModel.VAL_VALID){

            binding.signupNameTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_NAME_KEY]!!)

        }else if(valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!= SignUpViewModel.VAL_VALID){

            binding.signupPhoneTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!!)

        }else if(valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!= SignUpViewModel.VAL_VALID){

            binding.signupEmailTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!!)

        }else if (valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] != SignUpViewModel.VAL_VALID){

            binding.signupPasswordTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!)

            if(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY] == SignUpViewModel.PASSWORDS_DONT_MATCH){
                binding.signupConfirmPasswordTextLayout.error = getString(valMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!)
            }

        }else{

            binding.signupButton.visibility = View.GONE
            viewModel.signUpUser(binding.signupNameEditext.text.toString(),
                binding.signupPhoneEditext.text.toString(),
                binding.signupEmailEditext.text.toString(),
                binding.signupPasswordEditext.text.toString())

            EspressoTestingIdlingResource.increment()
            viewModel.getNetworkState()!!.observe(this, Observer { networkState->
                binding.networkState = networkState
                handleNetworkState(networkState)
            })
        }
    }

    private fun resetTextInputLayoutErrors(){

        binding.signupNameTextLayout.error = null
        binding.signupPhoneTextLayout.error = null
        binding.signupEmailTextLayout.error = null
        binding.signupConfirmPasswordTextLayout.error = null
        binding.signupPasswordTextLayout.error = null
    }


    override fun onDestroy() {

        viewModel.getNetworkState()?.removeObservers(this)
        super.onDestroy()
    }

}
