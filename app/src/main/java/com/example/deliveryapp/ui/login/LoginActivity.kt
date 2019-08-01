package com.example.deliveryapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.deliveryapp.R
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivityLoginBinding
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.utils.ViewModelFactory
import com.google.gson.Gson
import dagger.android.AndroidInjection
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import timber.log.Timber
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

        Timber.e(Gson().toJson(viewModel.currentUser))
        if(viewModel.currentUser!=null){
            goToNextActivity()
        }

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.lifecycleOwner = this

    }

    fun validateAndLoginUser(view: View){

        idlingResource?.increment()
        viewModel.validateLoginDetails(binding.loginEmailEditext.text.toString(),
            binding.loginEmailEditext.text.toString())

        viewModel.validationMap.observe(this, Observer { valMap-> processValidationMap(valMap, binding.loginEmailEditext.text.toString(),
            binding.loginPasswordEditext.text.toString())})

        viewModel.getNetworkState().observe(this, Observer { networkState->
            binding.networkState = networkState
            handleNetworkState(networkState)})

    }

    private fun handleNetworkState(networkState: NetworkState) {
        if(networkState.status == Status.SUCCESS){
            idlingResource?.decrement()
            goToNextActivity()
        }else if(networkState.status == Status.FAILED){
            idlingResource?.decrement()
            binding.loginButton.visibility = View.VISIBLE
        }
    }

    private fun goToNextActivity(){

        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun processValidationMap(valMap: WeakHashMap<String,String>, email:String, password:String){
        resetTextInputErrors()

        idlingResource?.decrement()
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
            else -> {

                idlingResource?.increment()
                binding.loginButton.visibility = View.GONE
                Timber.e("email: $email, password:$password")
                viewModel.loginUser(email,password)}
        }
    }

    private fun resetTextInputErrors(){

        binding.loginEmailTextLayout.error = null
        binding.loginPasswordEditext.error = null
    }

    fun goToSignUpActivity(view:View){
        startActivity(Intent(this,SignUpActivity::class.java))
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    override fun onDestroy() {
        viewModel.validationMap.removeObservers(this)
        viewModel.getNetworkState().removeObservers(this)
        super.onDestroy()
    }

    // idling resource for espresso tests
    var idlingResource: CountingIdlingResource? = null
}