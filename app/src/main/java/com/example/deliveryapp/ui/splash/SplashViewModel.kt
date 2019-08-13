package com.example.deliveryapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.UserRepository
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val userRepo:UserRepository) : ViewModel(){


    private var currentUser :LiveData<User>? = null

    fun getCurrentUser() : LiveData<User>?{
        currentUser = userRepo.getCurrentUser()
        return currentUser
    }

}