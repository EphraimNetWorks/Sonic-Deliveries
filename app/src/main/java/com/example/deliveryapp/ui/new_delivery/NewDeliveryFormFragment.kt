package com.example.deliveryapp.ui.new_delivery

import android.os.Bundle
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
import com.google.android.gms.maps.model.LatLng


class NewDeliveryFormFragment :Fragment(){

    lateinit var binding: FragmentNewDeliveryFormBinding

    private lateinit var viewModel:DeliveryFormViewModel

    private var mPickUpLocation :LatLng? = null
    private var mDestinationLocation :LatLng? = null
    private lateinit var mPickUpDate :MyDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState!=null){
            restoreState(savedInstanceState)
        }

        viewModel = ViewModelProviders.of(this).get(DeliveryFormViewModel::class.java)

        startObservers()
    }

    fun startObservers(){
        viewModel.validationMap.observe(this, Observer { validationMap-> handleDeliveryValMap(validationMap) })
    }

    fun stopObservers(){
        viewModel.validationMap.removeObservers(this)
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

    companion object{
        const val ITEM_NAME_STATE = "itemName"
        const val PICK_UP_STATE = "pickUp"
        const val DESTINATION_STATE = "destination"
        const val PICK_UP_DATE_STATE = "pick_up_date"
        const val ADDITIONAL_INFO_STATE = "additional info"

        fun newInstance():NewDeliveryFormFragment{

            return NewDeliveryFormFragment()
        }
    }
}