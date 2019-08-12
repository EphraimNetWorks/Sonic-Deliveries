package com.example.deliveryapp.ui.new_delivery

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


class NewDeliveryFormFragment :Fragment(),OnMapReadyCallback{

    lateinit var binding: FragmentNewDeliveryFormBinding

    private lateinit var viewModel:DeliveryFormViewModel
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

        binding.destinationSearchLocation.setOnClickListener { searchPlace(DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE) }

        binding.deliveryDateSelectButton.setOnClickListener { showDatePicker() }

        setUpMaps()
    }

    private fun startObservers(){

        viewModel.directionsResult.observe(this, Observer { directionResult-> updatePolyline(directionResult) })
    }

    private fun stopObservers(){
        viewModel.directionsResult.removeObservers(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        binding.mapsProgressBar.visibility = View.GONE

    }

    private fun showDatePicker(){
        DatePickerDialog(context!!,
            viewModel.dateListener,
            Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH).show()
    }

    private fun setUpMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        var mapFragment: SupportMapFragment? = activity!!.supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            val fragmentTransaction = childFragmentManager.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            fragmentTransaction.replace(R.id.map, mapFragment!!)
            fragmentTransaction.commit()
        }
        mapFragment.getMapAsync(this)

    }

    private fun searchPlace(PLACE_AUTOCOMPLETE_REQUEST_CODE: Int) {
        // Set the fields to specify which types of place data to return.
        val fields = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields)
            .build(context!!)

        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

    }

    private fun setPickUpPoint(data: Intent) {
        val place = Autocomplete.getPlaceFromIntent(data)

        val placeAddress = " ${place.name}, ${place.address}"
        //binding.pickUpAddressProgress.visibility = View.VISIBLE
        binding.pickUpPointEditext.setText(placeAddress)

        startPoint?.remove()

        this.startPoint = mMap.addMarker(MarkerOptions().position(place.latLng!!).title("PickUp Point"))
        viewModel.mPickUpLocation = place.latLng

        if(viewModel.mPickUpLocation!= null && viewModel.mDestinationLocation!=null) {
            queryDirections(viewModel.mPickUpLocation!!,viewModel.mDestinationLocation!!)
        }

    }

    private fun setDestinationPoint(data: Intent) {
        val place = Autocomplete.getPlaceFromIntent(data)

        val placeAddress = " ${place.name}, ${place.address}"
        //binding.destinationAddressProgress.visibility = View.VISIBLE
        binding.destinationEditext.setText(placeAddress)

        endPoint?.remove()

        this.endPoint = mMap.addMarker(MarkerOptions().position(place.latLng!!).title("Destination"))
        viewModel.mDestinationLocation = place.latLng

        if(viewModel.mPickUpLocation!= null && viewModel.mDestinationLocation!=null) {
            queryDirections(viewModel.mPickUpLocation!!,viewModel.mDestinationLocation!!)
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
        when {
            validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!= DeliveryFormViewModel.VAL_VALID ->
                binding.itemNameTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_ITEM_NAME]!!)

            validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_ADDRESS]!= DeliveryFormViewModel.VAL_VALID ->
                binding.pickUpPointTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_ADDRESS]!!)

            validationMap[DeliveryFormViewModel.VAL_MAP_DESTINATION_ADDRESS]!= DeliveryFormViewModel.VAL_VALID ->
                binding.destinationTextLayout.error = getString(validationMap[DeliveryFormViewModel.VAL_MAP_DESTINATION_ADDRESS]!!)

            validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_DATE]!= DeliveryFormViewModel.VAL_VALID -> {
                binding.deliveryDateSelectButton.setBackgroundColor(Color.RED)
                Toast.makeText(context!!, getString(validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_DATE]!!)
                    , Toast.LENGTH_LONG).show()
            }
        }

        if(viewModel.isNewDeliveryValid()){
            (activity as DeliveryFormValidation).onValidationSuccess()
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
        binding = FragmentNewDeliveryFormBinding.inflate(inflater,container,false)
        return binding.root
    }

    fun getNewDelivery():Delivery{
        return Delivery().apply {
            title = binding.itemNameEditext.text.toString()
            pickUpAddress = binding.pickUpPointEditext.text.toString()
            destinationAddress = binding.destinationEditext.text.toString()
            pickUpLocation = viewModel.mPickUpLocation
            destinationLocation = viewModel.mDestinationLocation
            pickUpTime = viewModel.mPickUpDate!!.timeStamp
            pickUpTimeDate = viewModel.mPickUpDate
            additionalInfo = binding.additionalInfoEdittext.text.toString()
        }
    }

    fun validateNewDelivery(){
        val valMap = viewModel.validateNewDelivery(binding.itemNameEditext.text.toString(),
            binding.pickUpPointEditext.text.toString(),
            binding.destinationEditext.text.toString(),
            viewModel.mPickUpDate?.getDateFormat1())

        handleDeliveryValMap(valMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                setPickUpPoint(data!!)
            }
            PICK_UP_PLACE_PICKER_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                setPickUpPoint(data!!)
            }
            DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                setDestinationPoint(data!!)
            }
            DESTINATION_PLACE_PICKER_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                setDestinationPoint(data!!)
            }
        }
    }

    companion object{
        const val ITEM_NAME_STATE = "itemName"
        const val PICK_UP_STATE = "pickUp"
        const val DESTINATION_STATE = "destination"
        const val PICK_UP_DATE_STATE = "pick_up_date"
        const val ADDITIONAL_INFO_STATE = "additional info"


        private const val PICK_UP_PLACE_PICKER_REQUEST = 10
        private const val PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 11
        private const val DESTINATION_PLACE_PICKER_REQUEST = 20
        private const val DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE = 21

        fun newInstance():NewDeliveryFormFragment{

            return NewDeliveryFormFragment()
        }
    }
}