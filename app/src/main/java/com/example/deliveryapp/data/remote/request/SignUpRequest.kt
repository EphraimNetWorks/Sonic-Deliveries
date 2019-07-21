package com.example.deliveryapp.data.remote.request

data class SignUpRequest(
    val name:String,
    val phone:String,
    val email:String,
    val password:String
)