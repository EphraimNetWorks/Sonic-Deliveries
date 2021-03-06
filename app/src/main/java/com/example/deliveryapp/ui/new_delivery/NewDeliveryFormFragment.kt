package com.example.deliveryapp.ui.new_delivery

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.Location
import com.example.deliveryapp.data.local.models.MyDate
import com.example.deliveryapp.databinding.FragmentNewDeliveryFormBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import dagger.android.support.AndroidSupportInjection
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class NewDeliveryFormFragment :Fragment(),OnMapReadyCallback{


    lateinit var binding: FragmentNewDeliveryFormBinding

    private val viewModel by viewModels<DeliveryFormViewModel>()

    private lateinit var mMap: GoogleMap

    private var startPoint: Marker? = null
    private var endPoint: Marker? = null
    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pickUpSearchLocation.setOnClickListener { searchPlace(PICK_UP_PLACE_AUTOCOMPLETE_REQUEST_CODE) }

        binding.destinationSearchLocation.setOnClickListener { searchPlace(DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE) }

        binding.deliveryDateSelectButton.setOnClickListener { showDatePicker() }

        setUpMaps()
    }

    private fun startObservers(){

        viewModel.directionsResult!!.observe(viewLifecycleOwner, { directionResult-> updatePolyline(directionResult) })
    }

    private fun stopObservers(){
        viewModel.directionsResult?.removeObservers(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        binding.mapsProgressBar.visibility = View.GONE

    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private val dateListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            viewModel.mPickUpDate = MyDate(year,monthOfYear+1,dayOfMonth)
            binding.deliveryDateSelectButton.text = viewModel.mPickUpDate!!.getDateFormat1()

        }

    private fun setUpMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        var mapFragment: SupportMapFragment? = requireActivity().supportFragmentManager
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

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withGeolocApiKey(getString(R.string.maps_api_key))
            .withGooglePlacesEnabled()
            .build(requireContext().applicationContext)

        startActivityForResult(locationPickerIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

    }

    private fun setPickUpPoint(data: Intent) {

        val latitude = data.getDoubleExtra(LATITUDE, 0.0)
        Timber.d("LATITUDE: $latitude")
        val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
        Timber.d("LONGITUDE: $longitude")
        val address = data.getStringExtra(LOCATION_ADDRESS)
        Timber.d("ADDRESS: $address")


        binding.pickUpPointEditext.setText(address)

        startPoint?.remove()

        viewModel.mPickUpLocation = Location(latitude,longitude)
        val markerLocation = LatLng(latitude,longitude)
        adjustMapCamera(viewModel.mPickUpLocation,viewModel.mDestinationLocation)
        this.startPoint = mMap.addMarker(MarkerOptions().position(markerLocation).title("PickUp Point"))

        if(viewModel.mPickUpLocation!= null && viewModel.mDestinationLocation!=null) {
            queryDirections(viewModel.mPickUpLocation!!,viewModel.mDestinationLocation!!)
        }

    }

    private fun setDestinationPoint(data: Intent) {

        val latitude = data.getDoubleExtra(LATITUDE, 0.0)
        Timber.d("LATITUDE: $latitude")
        val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
        Timber.d("LONGITUDE: $longitude")
        val address = data.getStringExtra(LOCATION_ADDRESS)
        Timber.d("ADDRESS: $address")
        binding.destinationEditext.setText(address)

        endPoint?.remove()

        viewModel.mDestinationLocation = Location(latitude,longitude)
        val markerLocation = LatLng(latitude,longitude)
        this.endPoint = mMap.addMarker(MarkerOptions().position(markerLocation).title("Destination"))

        adjustMapCamera(viewModel.mPickUpLocation,viewModel.mDestinationLocation)
        if(viewModel.mPickUpLocation!= null && viewModel.mDestinationLocation!=null) {
            queryDirections(viewModel.mPickUpLocation!!,viewModel.mDestinationLocation!!)
        }
    }

    private fun updatePolyline(results: DirectionsResult) {

        polyline?.remove()
        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        polyline =
            mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(ContextCompat.getColor(requireContext(),R.color.colorAccent)))
    }

    private fun queryDirections(pickUpLocation:Location, destinationLocation:Location) {

        viewModel.getDirections(pickUpLocation, destinationLocation, getString(R.string.maps_api_key))

        adjustMapCamera(pickUpLocation,destinationLocation)

    }

    private fun adjustMapCamera(pickUpLocation:Location?, destinationLocation:Location?){
        val builder = LatLngBounds.Builder()
        if(pickUpLocation!=null) builder.include(LatLng(pickUpLocation.latitude,pickUpLocation.longitude))
        if(destinationLocation!=null) builder.include(LatLng(destinationLocation.latitude,destinationLocation.longitude))
        val bounds = builder.build()

        val padding = 64 // offset from edges of the map in pixels
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
                Toast.makeText(requireContext(), getString(validationMap[DeliveryFormViewModel.VAL_MAP_PICK_UP_DATE]!!)
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

        if(savedInstanceState!=null){
            restoreState(savedInstanceState)
        }

        startObservers()

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