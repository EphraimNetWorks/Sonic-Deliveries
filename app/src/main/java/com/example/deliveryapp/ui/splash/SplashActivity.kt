package com.example.deliveryapp.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity(),Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(SplashViewModel::class.java)

        val currentUserLD = viewModel.getCurrentUser()

        if(currentUserLD == null){
            goToNextActivity(false)
        }else{
            currentUserLD.observe(this, Observer {
                if(it!=null)goToNextActivity(true)
                else
                goToNextActivity(false)})
        }

    }

    fun goToNextActivity(isLoggedIn: Boolean){
        Handler().postDelayed({
            val intent = if(isLoggedIn) MainActivity.newInstance(this,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN)
            else
                LoginActivity.newInstance(this,MainActivity.SALUTATION_TYPE_NEW_LOGIN)

            startActivity(intent)
        },3000)

    }
}
