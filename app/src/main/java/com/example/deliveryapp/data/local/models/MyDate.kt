package com.example.deliveryapp.data.local.models

import timber.log.Timber
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyDate : Serializable{
    var timeStamp:Long

    constructor(timeStamp:Long){
        this.timeStamp = timeStamp
    }

    constructor(year:Int, month: Int, day:Int){
        this.timeStamp = convertDateToTimeStamp(year,month, day)
    }

    // format 1 = Wednesday Jan 12,2039
    fun getDateFormat1():String{

        val format1 = SimpleDateFormat(FORMAT_1)

        return format1.format(Date(timeStamp))
    }

    private fun convertDateToTimeStamp(year:Int, month: Int, day:Int):Long{

        val date = getDateFromString("$year-$month-$day 08:00:00", "yyyy-MM-dd HH:mm:ss")

        return date.time
    }


    private fun getDateFromString(startDateString: String, format: String): Date {
        try {
            val formatter = SimpleDateFormat(format, Locale.ENGLISH)
            return formatter.parse(startDateString)!!
        } catch (e: ParseException) {
            Timber.e(e.message)
        }

        return Date(0)
    }

    companion object{
        const val FORMAT_1 = "EEEE MMMM dd,yyyy"
    }
}