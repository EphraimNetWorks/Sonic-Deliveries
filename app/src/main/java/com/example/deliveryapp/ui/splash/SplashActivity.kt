package com.example.deliveryapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.example.deliveryapp.R
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.ui.onboarding.OnBoardingActivity
import com.example.deliveryapp.utils.ViewModelFactory
import javax.inject.Inject
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

        if(isFirstTime()){
            goToNextActivity( true, isLoggedIn = false)

        }else{
            checkLoginStatus()
        }


    }

    private fun isFirstTime():Boolean{
        val prefs = getSharedPreferences("first_time", Context.MODE_PRIVATE)

        return prefs.getBoolean("isFirstTime", true)
    }

    private fun updateFirstTime(){
        val editor = getSharedPreferences("first_time", Context.MODE_PRIVATE).edit()

        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }

    private fun checkLoginStatus(){
        val currentUserLD = viewModel.currentUser
        if(currentUserLD == null){
            goToNextActivity(false, isLoggedIn = false)
        }else{
            currentUserLD.observe(this, Observer {
                if(it!=null)goToNextActivity(false, isLoggedIn = true)
                else
                    goToNextActivity(false, isLoggedIn = false)})
        }
    }

    private fun goToNextActivity(isFirstTime:Boolean, isLoggedIn: Boolean){
        Handler().postDelayed({
            val intent = when {
                isFirstTime -> Intent(this,OnBoardingActivity::class.java)
                isLoggedIn -> MainActivity.newInstance(this,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN)
                else -> LoginActivity.newInstance(this,MainActivity.SALUTATION_TYPE_NEW_LOGIN)
            }

            if(isFirstTime) updateFirstTime()

            startActivity(intent)
        },3000)

    }
}
