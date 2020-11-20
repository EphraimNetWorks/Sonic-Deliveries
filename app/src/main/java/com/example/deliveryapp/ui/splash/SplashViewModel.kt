package com.example.deliveryapp.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.UserRepository
import dagger.hilt.android.scopes.ActivityScoped


@ActivityScoped
class SplashViewModel @ViewModelInject constructor(private val userRepo:UserRepository) : ViewModel(){


    val currentUser :LiveData<User>?
        get() = userRepo.getCurrentUser()


}