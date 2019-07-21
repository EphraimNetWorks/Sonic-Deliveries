package com.example.deliveryapp.data.local.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Delivery {

    @PrimaryKey
    @NonNull
    var id:String = ""

    var createdAt:String? = null

    var title: String? = null

    var origin: String? = null

    var destination: String? = null

    var deliveryStatus: String = STATUS_PENDING

    var deliveredAt:String? = null

    companion object{
        const val STATUS_COMPLETED = "completed"
        const val STATUS_PENDING = "pending"
        const val STATUS_CANCELLED = "cancelled"
    }
}