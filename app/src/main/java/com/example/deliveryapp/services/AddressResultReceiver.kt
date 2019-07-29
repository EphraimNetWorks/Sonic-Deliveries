package com.example.deliveryapp.services

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import androidx.lifecycle.MutableLiveData

class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

    internal var addressFound = MutableLiveData<String>()

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

        if (resultData == null) {
            return
        }

        // Display the address string
        // or an error message sent from the intent service.
        addressFound.postValue(resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY))


    }

}
