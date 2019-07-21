package com.example.deliveryapp.data.remote

interface ApiCallback<T> {

    fun onSuccess( result: T)

    fun onFailed( errMsg: String)
}