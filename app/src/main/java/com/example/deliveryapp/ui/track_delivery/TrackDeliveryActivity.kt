package com.example.deliveryapp.ui.track_delivery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.ActivityTrackDeliveryBinding

class TrackDeliveryActivity : AppCompatActivity() {

    lateinit var mDelivery: Delivery
    lateinit var binding:ActivityTrackDeliveryBinding

    private lateinit var timelineAdapter:DeliveryTimelineAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_delivery)

        mDelivery = intent.getSerializableExtra(DELIVERY_EXTRA) as Delivery

        binding.delivery = mDelivery

        setUpDeliveryTimeline()

    }

    private fun setUpDeliveryTimeline(){

        timelineAdapter = DeliveryTimelineAdapter(mDelivery)
        layoutManager = LinearLayoutManager(this)

        binding.deliveryTimelineRecycler.adapter = timelineAdapter
        binding.deliveryTimelineRecycler.layoutManager = layoutManager

    }

    companion object{
        private const val DELIVERY_EXTRA = "delivery"

        fun newInstance(context: Context,delivery: Delivery): Intent {
            val intent = Intent(context,TrackDeliveryActivity::class.java)
            intent.putExtra(DELIVERY_EXTRA,delivery)
            return intent
        }
    }

    var idlingResource:CountingIdlingResource? = null
}
