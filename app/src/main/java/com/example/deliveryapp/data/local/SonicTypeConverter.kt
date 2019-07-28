package com.example.deliveryapp.data.local


import androidx.room.TypeConverter
import com.example.deliveryapp.data.local.models.MyDate
import com.google.gson.Gson
import com.google.type.LatLng

class SonicTypeConverter {

    @TypeConverter
    fun myDateToString(myDate: MyDate): String{
        return Gson().toJson(myDate)
    }

    @TypeConverter
    fun stringToMyDate(string: String): MyDate {
        return Gson().fromJson(string, MyDate::class.java)
    }

    @TypeConverter
    fun locationDataToString(locationData: LatLng): String{
        return Gson().toJson(locationData)
    }

    @TypeConverter
    fun stringToLocationDate(string: String): LatLng {
        return Gson().fromJson(string, LatLng::class.java)
    }
}
