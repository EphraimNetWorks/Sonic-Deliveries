package com.example.deliveryapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.VisibleForTesting
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
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.utils.EspressoTestingIdlingResource
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(LoginViewModel::class.java)

        viewModel.initializeCurrentUser()?.observe(this, Observer {
            if(it!=null){
                goToNextActivity(true)
            }
        })

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.lifecycleOwner = this

    }

    fun validateAndLoginUser(view: View){

        val valMap = viewModel.validateLoginDetails(binding.loginEmailEditext.text.toString(),
            binding.loginPasswordEditext.text.toString())

        processValidationMap(valMap, binding.loginEmailEditext.text.toString(),
            binding.loginPasswordEditext.text.toString())

    }

    private fun handleNetworkState(networkState: NetworkState) {
        when {
            networkState.status == Status.SUCCESS -> {
                Timber.d("log in success")
                goToNextActivity(false)
                EspressoTestingIdlingResource.decrement()
            }
            networkState.status == Status.RUNNING -> {
                binding.loginButton.visibility = View.GONE
                Timber.d("logging in")
            }
            networkState.status == Status.FAILED -> {
                Timber.d("log in failed")
                binding.loginButton.visibility = View.VISIBLE
                EspressoTestingIdlingResource.decrement()
            }
        }
    }

    private fun goToNextActivity(isAlreadyLoggedIn:Boolean){

        if(intent.hasExtra(MainActivity.EXTRA_SALUTATION_TYPE)){
            startActivity(MainActivity.newInstance(this,intent.getIntExtra(MainActivity.EXTRA_SALUTATION_TYPE,
                MainActivity.SALUTATION_TYPE_NEW_LOGIN)))
        }else {
            startActivity(
                MainActivity.newInstance(
                    this, if (isAlreadyLoggedIn)
                        MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN
                    else
                        MainActivity.SALUTATION_TYPE_NEW_LOGIN
                )
            )
        }
    }

    private fun processValidationMap(valMap: HashMap<String,Int>, email:String, password:String){
        resetTextInputErrors()

        when {
            valMap[LoginViewModel.VAL_MAP_EMAIL_KEY]!= LoginViewModel.VAL_VALID -> {

                binding.loginEmailTextLayout.error = getString(valMap[LoginViewModel.VAL_MAP_EMAIL_KEY]!!)

            }
            valMap[LoginViewModel.VAL_MAP_PASSWORD_KEY] != LoginViewModel.VAL_VALID -> {

                binding.loginPasswordTextLayout.error = getString(valMap[LoginViewModel.VAL_MAP_PASSWORD_KEY]!!)

            }
            else -> {

                viewModel.loginUser(email,password)

                EspressoTestingIdlingResource.increment()
                viewModel.getNetworkState()!!.observe(this, Observer { networkState->
                    binding.networkState = networkState
                    handleNetworkState(networkState)})
            }
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
        viewModel.getNetworkState()?.removeObservers(this)
        super.onDestroy()
    }

    companion object{
        fun newInstance(context: Context, salutationType:Int): Intent {
            return Intent(context,LoginActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_SALUTATION_TYPE, salutationType)
            }
        }
    }

}