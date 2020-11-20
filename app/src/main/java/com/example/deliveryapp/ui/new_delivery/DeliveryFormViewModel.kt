package com.example.deliveryapp.ui.new_delivery


import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.models.Location
import com.google.maps.model.DirectionsResult
import kotlin.collections.HashMap
import com.example.deliveryapp.data.local.models.MyDate
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import dagger.hilt.android.scopes.ActivityScoped


@ActivityScoped
class DeliveryFormViewModel @ViewModelInject constructor(private val deliveryRepo:DeliveryRepository) :ViewModel(){


    lateinit var validationMap: HashMap<String, Int>

    var mPickUpLocation :Location? = null
    var mDestinationLocation :Location? = null
    var mPickUpDate : MyDate? = null

    var directionsResult:LiveData<DirectionsResult>? = null

    init {
        initializeValidationMap()

        val direcRes = deliveryRepo.directionResults

        this.directionsResult = Transformations.map(direcRes) { it }
    }

    fun isNewDeliveryValid():Boolean{
        for(obj in validationMap.values){
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
        validationMap = initialMap
    }

    fun validateNewDelivery(itemName: String,
                            pickUpAddress: String,
                            destinationAddress: String,
                            pickUpDate: String?):HashMap<String,Int>{
        validateItemName(itemName)
        validatePickUpAddress(pickUpAddress)
        validateDestinationAddress(destinationAddress)
        validatePickUpDate(pickUpDate)

        return validationMap
    }

    fun validateItemName(itemName:String){
        var validationMessage = VAL_VALID
        if(itemName.isEmpty()){
            validationMessage = EMPTY_ITEM_NAME
        }

        validationMap[VAL_MAP_ITEM_NAME] = validationMessage

    }

    fun validatePickUpAddress(pickUpAddress:String){
        var validationMessage = VAL_VALID
        if(pickUpAddress.isEmpty()){
            validationMessage = EMPTY_PICK_UP_ADDRESS
        }

        validationMap[VAL_MAP_PICK_UP_ADDRESS] = validationMessage

    }

    fun validateDestinationAddress(destinationAddress:String){
        var validationMessage = VAL_VALID
        if(destinationAddress.isEmpty()){
            validationMessage = EMPTY_DESTINATION_ADDRESS
        }

        validationMap[VAL_MAP_DESTINATION_ADDRESS] = validationMessage

    }

    fun validatePickUpDate(pickUpDate:String?){
        var validationMessage = VAL_VALID
        if(pickUpDate.isNullOrEmpty()){
            validationMessage = EMPTY_PICK_UP_DATE
        }

        validationMap[VAL_MAP_PICK_UP_DATE] = validationMessage

    }

    fun getDirections(origin: Location, destination: Location, apiKey: String) {

        deliveryRepo.getDirectionResults(origin,destination,apiKey)
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