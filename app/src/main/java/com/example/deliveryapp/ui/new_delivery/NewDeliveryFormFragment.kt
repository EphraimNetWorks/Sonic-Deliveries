package com.example.deliveryapp.ui.new_delivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.MyDate
import com.example.deliveryapp.databinding.FragmentNewDeliveryFormBinding
import com.example.deliveryapp.services.FetchAddressIntentService
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import timber.log.Timber


class NewDeliveryFormFragment :Fragment(),OnMapReadyCallback{

    lateinit var binding: FragmentNewDeliveryFormBinding

    private lateinit var viewModel:DeliveryFormViewModel

    private var mPickUpLocation :LatLng? = null
    private var mDestinationLocation :LatLng? = null
    private lateinit var mPickUpDate :MyDate
    private lateinit var mMap: GoogleMap

    private var startPoint: Marker? = null
    private var endPoint: Marker? = null
    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState!=null){
            restoreState(savedInstanceState)
        }

        viewModel = ViewModelProviders.of(this).get(DeliveryFormViewModel::class.java)

        startObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pickUpSearchLocation.setOnClickListener { searchPlace(PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE) }

        binding.pickUpSelectLocationFromMap.setOnClickListener { pickPointFromMap(PICK_UP_PLACE_PICKER_REQUEST) }

        binding.destinationSearchLocation.setOnClickListener { searchPlace(DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE) }

        binding.destinationSelectLocationFromMap.setOnClickListener { pickPointFromMap(DESTINATION_PLACE_PICKER_REQUEST) }

        setUpMaps()
    }

    private fun startObservers(){
        viewModel.validationMap.observe(this, Observer { validationMap-> handleDeliveryValMap(validationMap) })
        viewModel.directionsResult.observe(this, Observer { directionResult-> updatePolyline(directionResult) })
    }

    private fun stopObservers(){
        viewModel.validationMap.removeObservers(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

    }

    private fun setUpMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment: SupportMapFragment? = activity!!.supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            val fragmentTransaction = childFragmentManager.beginTransaction()
            val mapFragment = SupportMapFragment.newInstance()
            fragmentTransaction.replace(R.id.map, mapFragment!!)
            fragmentTransaction.commit()
        }
        mapFragment!!.getMapAsync(this)

    }

    private fun searchPlace(PLACE_AUTOCOMPLETE_REQUEST_CODE: Int) {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .build(activity)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Timber.e(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Timber.e(e)
        }

    }

    private fun pickPointFromMap(PLACE_PICKER_REQUEST: Int) {
        try {

            val builder = PlacePicker.IntentBuilder()

            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            Timber.e(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Timber.e(e)
        }

    }

    private fun setPickUpPoint(data: Intent?) {
        val place = PlacePicker.getPlace(context, data)
        if(place.name.isNullOrEmpty()){
            binding.pickUpAddressProgress.visibility = View.VISIBLE
            startFetchAddressService(place.latLng, PICK_UP_ADDRESS_REQUEST)
        }else {
            binding.destinationAddressProgress.visibility = View.VISIBLE
            binding.pickUpPointEditext.setText(place.name)
        }

        startPoint?.remove()

        this.startPoint = mMap.addMarker(MarkerOptions().position(place.latLng).title("PickUp Point"))
        mPickUpLocation = place.latLng

        if(mPickUpLocation!= null && mDestinationLocation!=null) {
            queryDirections(mPickUpLocation!!,mDestinationLocation!!)
        }

    }

    private fun setDestinationPoint(data: Intent?) {
        val place = PlacePicker.getPlace(context, data)
        if(place.name.isNullOrEmpty()){
            startFetchAddressService(place.latLng, DESTINATION_ADDRESS_REQUEST)
        }else {
            binding.destinationEditext.setText(place.name)
        }

        endPoint?.remove()

        this.endPoint = mMap.addMarker(MarkerOptions().position(place.latLng).title("Destination"))
        mDestinationLocation = place.latLng

        if(mPickUpLocation!= null && mDestinationLocation!=null) {
            queryDirections(mPickUpLocation!!,mDestinationLocation!!)
        }
    }

    private fun updatePolyline(results: DirectionsResult) {

        polyline?.remove()
        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        polyline =
            mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(resources.getColor(R.color.colorAccent)))
    }

    private fun queryDirections(pickUpLocation:LatLng, destinationLocation:LatLng) {

        val origin = com.google.maps.model.LatLng(pickUpLocation.latitude, pickUpLocation.longitude)
        val destination = com.google.maps.model.LatLng(destinationLocation.latitude, destinationLocation.longitude)
        viewModel.getDirections(origin, destination, getString(R.string.google_maps_key))

        adjustMapCamera(pickUpLocation,destinationLocation)

    }

    private fun adjustMapCamera(pickUpLocation:LatLng, destinationLocation:LatLng){
        val builder = LatLngBounds.Builder()
        builder.include(pickUpLocation)
        builder.include(destinationLocation)
        val bounds = builder.build()

        val padding = 32 // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.animateCamera(cu)
    }

    private fun handleDeliveryValMap(validationMap: HashMap<String, Int>) {
        if(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!= DeliveryFormViewModel.VAL_VALID){

            binding.itemNameTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!!)

        }else if(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!= DeliveryFormViewModel.VAL_VALID){

            binding.itemNameTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!!)

        }else if(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!= DeliveryFormViewModel.VAL_VALID){

            binding.itemNameTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!!)

        }else if(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!= DeliveryFormViewModel.VAL_VALID){

            binding.itemNameTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!!)

        }
    }

    override fun onDestroy() {
        stopObservers()
        super.onDestroy()
    }

    private fun restoreState(savedInstanceState: Bundle){
        binding.itemNameEditext.text = SpannableStringBuilder(savedInstanceState.getString(ITEM_NAME_STATE))
        binding.pickUpPointEditext.text = SpannableStringBuilder(savedInstanceState.getString(PICK_UP_STATE))
        binding.destinationEditext.text = SpannableStringBuilder(savedInstanceState.getString(DESTINATION_STATE))
        binding.itemNameEditext.text = SpannableStringBuilder(savedInstanceState.getString(PICK_UP_DATE_STATE))
        binding.additionalInfoEdittext.text = SpannableStringBuilder(savedInstanceState.getString(ADDITIONAL_INFO_STATE))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ITEM_NAME_STATE, binding.itemNameEditext.text.toString())
        outState.putString(PICK_UP_STATE, binding.pickUpPointEditext.text.toString())
        outState.putString(DESTINATION_STATE, binding.destinationEditext.text.toString())
        outState.putString(PICK_UP_DATE_STATE, "")
        outState.putString(ADDITIONAL_INFO_STATE, binding.additionalInfoEdittext.text.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentNewDeliveryFormBinding>(inflater,
            R.layout.fragment_new_delivery_form, container, false)
        return binding.root
    }

    fun getNewDelivery():Delivery{
        return Delivery().apply {
            title = binding.itemNameEditext.text.toString()
            pickUpAddress = binding.pickUpPointEditext.text.toString()
            destinationAddress = binding.destinationEditext.text.toString()
            pickUpLocation = mPickUpLocation
            destinationLocation = mDestinationLocation
            pickUpTime = mPickUpDate.timeStamp
            pickUpTimeDate = mPickUpDate
            additionalInfo = binding.additionalInfoEdittext.text.toString()
        }
    }

    fun getNewDeliveryValidity():LiveData<Boolean>{
        return viewModel.newDeliveryIsValid
    }

    fun validateNewDelivery(){
        viewModel.validateNewDelivery(binding.itemNameEditext.text.toString(),
            binding.pickUpPointEditext.text.toString(),
            binding.destinationEditext.text.toString(),
            mPickUpDate.getDateFormat1())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                setPickUpPoint(data)
            }
            PICK_UP_PLACE_PICKER_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                setPickUpPoint(data)
            }
            DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                setDestinationPoint(data)
            }
            DESTINATION_PLACE_PICKER_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                setDestinationPoint(data)
            }
        }
    }

    companion object{
        const val ITEM_NAME_STATE = "itemName"
        const val PICK_UP_STATE = "pickUp"
        const val DESTINATION_STATE = "destination"
        const val PICK_UP_DATE_STATE = "pick_up_date"
        const val ADDITIONAL_INFO_STATE = "additional info"

        const val PICK_UP_ADDRESS_RESULT_DATA_KEY = "pickUpAddressResult"
        const val DESTINATION_ADDRESS_RESULT_DATA_KEY = "destinationAddressResult"


        private const val PICK_UP_PLACE_PICKER_REQUEST = 10
        private const val PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 11
        private const val DESTINATION_PLACE_PICKER_REQUEST = 20
        private const val DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE = 21
        private const val PICK_UP_ADDRESS_REQUEST = 30
        private const val DESTINATION_ADDRESS_REQUEST = 31

        fun newInstance():NewDeliveryFormFragment{

            return NewDeliveryFormFragment()
        }
    }


    private fun startFetchAddressService(location:LatLng, requestCode: Int) {

        val resultReceiver = AddressResultReceiver(Handler(),requestCode)

        val intent = Intent(context, FetchAddressIntentService::class.java)
        intent.putExtra(FetchAddressIntentService.RECEIVER, resultReceiver)
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location)
        context?.startService(intent)
    }

    inner class AddressResultReceiver(handler: Handler, private val requestCode: Int) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            if (resultData == null) {
                return
            }

            // Display the address string
            // or an error message sent from the intent service.

            if(requestCode == PICK_UP_ADDRESS_REQUEST) {
                val address = resultData.getString(PICK_UP_ADDRESS_RESULT_DATA_KEY)
                if (address != null) {
                    Timber.d("pick up location address: $address")
                    binding.pickUpPointEditext.setText(address)
                } else {
                    Timber.d("pick up location address: address null")
                }

                binding.pickUpAddressProgress.visibility = View.GONE
            }else if(requestCode == DESTINATION_ADDRESS_REQUEST){
                val address = resultData.getString(DESTINATION_ADDRESS_RESULT_DATA_KEY)

                if (address != null) {
                    Timber.d("destination location address: $address")
                    binding.destinationEditext.setText(address)
                } else {
                    Timber.d("destination location address: address null")
                }

                binding.pickUpAddressProgress.visibility = View.VISIBLE
            }

        }
    }
}