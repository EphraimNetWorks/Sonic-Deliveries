package com.example.deliveryapp.services

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.os.ResultReceiver
import android.text.TextUtils
import timber.log.Timber

import java.io.IOException
import java.util.ArrayList
import java.util.Locale

class FetchAddressIntentService : IntentService {

    protected var receiver: ResultReceiver? = null

    constructor() : super("ReverseGeocodingService") {}
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    constructor(name: String) : super(name) {}

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onHandleIntent(intent: Intent?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        if (intent == null) {
            return
        }
        var errorMessage = ""

        // Get the location passed to this service through an extra.
        val location = intent.getParcelableExtra<Location>(
            LOCATION_DATA_EXTRA
        )
        this.receiver = intent.getParcelableExtra(
            RECEIVER
        )

        // ...

        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocation(
                location!!.latitude,
                location.longitude,
                // In this sample, get just a single address.
                1
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "Unable to get"
            Timber.e(errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid lat and long used"
            Timber.e(
                errorMessage + ". " +
                        "Latitude = " + location!!.latitude +
                        ", Longitude = " +
                        location.longitude, illegalArgumentException
            )
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found"
                Timber.e(errorMessage)
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            val addressFragments = ArrayList<String>()

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (i in 0..address.maxAddressLineIndex) {
                addressFragments.add(address.getAddressLine(i))
            }

            Timber.i("Address succesfully retreived: ")
            deliverResultToReceiver(
                SUCCESS_RESULT,
                TextUtils.join(
                    System.getProperty("line.separator") as CharSequence,
                    addressFragments
                )
            )
        }
    }


    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(RESULT_DATA_KEY, message)
        receiver!!.send(resultCode, bundle)
    }

    companion object{
        const val RESULT_DATA_KEY = "addressResult"
        const val LOCATION_DATA_EXTRA = "locationData"
        const val RECEIVER = "receiver"

        const val SUCCESS_RESULT = 10002
        const val FAILURE_RESULT = 10001
    }
}
