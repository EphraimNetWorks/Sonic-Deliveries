package com.example.deliveryapp.data.local.models

import java.io.Serializable

class MyDate(var timeStamp:Long) : Serializable{
    // format 1 = Wednesday Jan 12,2039
    fun getDateFormat1():String{
        return "Tuesday January 1,2019"
    }
}