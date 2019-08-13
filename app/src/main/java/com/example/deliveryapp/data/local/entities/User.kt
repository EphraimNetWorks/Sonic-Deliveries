package com.example.deliveryapp.data.local.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User {

    @PrimaryKey @NonNull
    var id = ""

    var name:String? = null

    var phone:String? = null

    var email:String?= null

    var profilePicUrl:String? = null

    override fun equals(other: Any?): Boolean {
        return if(other is User){
            other.id == id
        }else false
    }
}