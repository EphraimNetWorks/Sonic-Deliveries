package com.example.deliveryapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.ActivityMainBinding
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import java.lang.IllegalArgumentException
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        AndroidInjection.inject(this)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(MainViewModel::class.java)

        viewModel.initMyDeliveries()


        binding.placedDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.inTransitDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.completedDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.newDeliveryFab.setOnClickListener {
            startActivity(NewDeliveryActivity.newInstance(this))
        }

    }

    private fun startObservers(){
        viewModel.mostRecentDelivery.observe(this, Observer { recentDelivery->
            binding.summaryTextview.text = getSummaryMessage(recentDelivery)
        })

    }

    private fun removeObservers(){
        viewModel.mostRecentDelivery.removeObservers(this)
    }

    private fun getSummaryMessage(recentDelivery: Delivery):String{

        return when(recentDelivery.deliveryStatus){
            Delivery.STATUS_PLACED -> "${getString(R.string.placed_summary_message_prefix)} ${recentDelivery.title}" +
                    " ${getString(R.string.placed_summary_message_suffix)} ${recentDelivery.deliveryTimeDate!!.getDateFormat1()}"
            Delivery.STATUS_IN_TRANSIT -> getString(R.string.in_transit_summary_message) +
                    " ${recentDelivery.deliveryTimeDate!!.getDateFormat1()}"
            Delivery.STATUS_COMPLETED -> "${recentDelivery.title} ${getString(R.string.delivered_summary_message)}" +
                    " ${recentDelivery.deliveryTimeDate!!.getDateFormat1()}"
            Delivery.STATUS_CANCELLED -> "${recentDelivery.title}${getString(R.string.cancelled_summary_message)}" +
                    " ${recentDelivery.deliveryTimeDate!!.getDateFormat1()}"
            else-> throw IllegalArgumentException("unknown delivery status")
        }
    }

    override fun onStart() {
        startObservers()
        super.onStart()
    }

    override fun onStop() {
        removeObservers()
        super.onStop()
    }

}
