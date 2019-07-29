package com.example.deliveryapp.ui.new_delivery

import android.hardware.SensorAdditionalInfo
import androidx.annotation.StringRes
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class DeliveryFormViewModel :ViewModel(){


    var validationMap: MutableLiveData<HashMap<String, Int>> = MutableLiveData()

    var directionsResult:MutableLiveData<DirectionsResult> = MutableLiveData()

    var newDeliveryIsValid: LiveData<Boolean> = MutableLiveData()

    init {
        initializeValidationMap()

        newDeliveryIsValid = Transformations.map(validationMap){ checkIfDeliveryIsValid() }
    }

    private fun checkIfDeliveryIsValid():Boolean{
        for(obj in validationMap.value!!.values ){
            if(obj != VAL_VALID) return false
        }
        return true
    }

    private fun initializeValidationMap(){
        val initialMap = HashMap<String,Int>()
        initialMap[VAL_MAP_ITEM_NAME] = VAL_DEFAULT
        initialMap[VAL_MAP_PICK_UP_ADDRESS] = VAL_DEFAULT
        initialMap[VAL_MAP_DESTINATION_ADDRESS] = VAL_DEFAULT
        initialMap[VAL_MAP_PICK_UP_DATE] = VAL_DEFAULT
        validationMap.value = initialMap
    }

    fun validateNewDelivery(itemName: String,
                            pickUpAddress: String,
                            destinationAddress: String,
                            pickUpDate: String){
        validateItemName(itemName)
        validatePickUpAddress(pickUpAddress)
        validateDestinationAddress(destinationAddress)
        validatePickUpDate(pickUpDate)

    }

    fun validateItemName(itemName:String){
        var validationMessage = VAL_VALID
        if(itemName.isEmpty()){
            validationMessage = EMPTY_ITEM_NAME
        }
        val map = validationMap.value!!
        map[VAL_MAP_ITEM_NAME] = validationMessage
        validationMap.value = map

    }

    fun validatePickUpAddress(pickUpAddress:String){
        var validationMessage = VAL_VALID
        if(pickUpAddress.isEmpty()){
            validationMessage = EMPTY_PICK_UP_ADDRESS
        }
        val map = validationMap.value!!
        map[VAL_MAP_ITEM_NAME] = validationMessage
        validationMap.value = map

    }

    fun validateDestinationAddress(destinationAddress:String){
        var validationMessage = VAL_VALID
        if(destinationAddress.isEmpty()){
            validationMessage = EMPTY_DESTINATION_ADDRESS
        }
        val map = validationMap.value!!
        map[VAL_MAP_ITEM_NAME] = validationMessage
        validationMap.value = map

    }

    fun validatePickUpDate(pickUpDate:String){
        var validationMessage = VAL_VALID
        if(pickUpDate.isEmpty()){
            validationMessage = EMPTY_PICK_UP_DATE
        }
        val map = validationMap.value!!
        map[VAL_MAP_ITEM_NAME] = validationMessage
        validationMap.value = map

    }

    fun getDirections(origin: com.google.maps.model.LatLng, destination: com.google.maps.model.LatLng, apiKey: String) {
        val geoApiContext = GeoApiContext()
        geoApiContext.setQueryRateLimit(3)
            .setApiKey(apiKey)
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS)

        DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING).origin(origin)
            .destination(destination).departureTime(DateTime())
            .setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    directionsResult.postValue(result)
                    Timber.e("Get Directions Result success")
                }

                override fun onFailure(e: Throwable) {
                    Timber.e("Get Directions Result failed with error: %s", e.message)
                }
            })
    }

    companion object{

        const val VAL_MAP_ITEM_NAME = "itemName"
        const val VAL_MAP_PICK_UP_ADDRESS = "pickUpAddress"
        const val VAL_MAP_DESTINATION_ADDRESS= "destinationAddress"
        const val VAL_MAP_PICK_UP_DATE = "pickUpDate"

        @StringRes
        const val EMPTY_ITEM_NAME = R.string.empty_item_name_error
        @StringRes
        const val EMPTY_PICK_UP_ADDRESS = R.string.empty_pickup_address_error
        @StringRes
        const val EMPTY_DESTINATION_ADDRESS = R.string.empty_destination_address_error
        @StringRes
        const val EMPTY_PICK_UP_DATE = R.string.empty_pick_up_date_error
        const val VAL_VALID = 1
        const val VAL_DEFAULT = 0
    }

}