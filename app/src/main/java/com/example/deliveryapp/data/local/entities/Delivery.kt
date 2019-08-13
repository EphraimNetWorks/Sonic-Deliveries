package com.example.deliveryapp.data.local.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.deliveryapp.data.local.models.MyDate
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity
class Delivery : Serializable{

    @PrimaryKey
    @NonNull
    var id:String = ""

    var createdAt:Long? = null

    var updatedAt:Long? = null

    var title: String? = null

    var pickUpAddress: String? = null

    @Transient
    var pickUpLocation: LatLng? = null

    var destinationAddress: String?= null

    @Transient
    var destinationLocation: LatLng? = null

    var deliveryStatus: Int = STATUS_PLACED

    var estimatedTimeOfArrival:Long? = null
        set(value) {
            estimatedTimeOfArrivalDate = MyDate(value!!)
            field = value
        }

    var estimatedTimeOfArrivalDate: MyDate? = null

    var pickUpTime:Long? = null
        set(value) {
            pickUpTimeDate = MyDate(value!!)
            field = value
        }

    var pickUpTimeDate: MyDate? = null

    var deliveryTime:Long? = null
        set(value) {
            deliveryTimeDate = MyDate(value!!)
            field = value
        }

    var deliveryTimeDate: MyDate? = null

    var additionalInfo:String = "None"

    companion object{
        const val STATUS_COMPLETED = 2
        const val STATUS_IN_TRANSIT = 1
        const val STATUS_CANCELLED = 3
        const val STATUS_PLACED = 0
    }
}