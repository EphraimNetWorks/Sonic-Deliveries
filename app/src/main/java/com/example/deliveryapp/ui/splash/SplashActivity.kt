package com.example.deliveryapp.ui.splash

import android.content.Intent
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
import com.example.deliveryapp.ui.onboarding.OnBoardingActivity
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject
import android.content.SharedPreferences
import android.content.Context
import androidx.lifecycle.ViewModelProvider


class SplashActivity : AppCompatActivity(),Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(this,viewModelFactory).get(SplashViewModel::class.java)

        if(isfirstTime()){
            goToNextActivity( true, isLoggedIn = false)

        }else{
            checkLoginStatus()
        }


    }

    fun isfirstTime():Boolean{
        val prefs = getSharedPreferences("first_time", Context.MODE_PRIVATE)

        return prefs.getBoolean("isFirstTime", true)
    }

    fun updateFirstTime(){
        val editor = getSharedPreferences("first_time", Context.MODE_PRIVATE).edit()

        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }

    fun checkLoginStatus(){
        val currentUserLD = viewModel.getCurrentUser()
        if(currentUserLD == null){
            goToNextActivity(false, isLoggedIn = false)
        }else{
            currentUserLD.observe(this, Observer {
                if(it!=null)goToNextActivity(false, isLoggedIn = true)
                else
                    goToNextActivity(false, isLoggedIn = false)})
        }
    }

    fun goToNextActivity(isFirstTime:Boolean, isLoggedIn: Boolean){
        Handler().postDelayed({
            val intent = if(isFirstTime)
                Intent(this,OnBoardingActivity::class.java)
            else if(isLoggedIn)
                MainActivity.newInstance(this,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN)
            else
                LoginActivity.newInstance(this,MainActivity.SALUTATION_TYPE_NEW_LOGIN)

            if(isFirstTime) updateFirstTime()

            startActivity(intent)
        },3000)

    }
}
