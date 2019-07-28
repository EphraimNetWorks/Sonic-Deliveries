package com.example.deliveryapp.ui.track_delivery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.ActivityTrackDeliveryBinding

class TrackDeliveryActivity : AppCompatActivity() {

    lateinit var mDelivery: Delivery
    lateinit var binding:ActivityTrackDeliveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_delivery)

        mDelivery = intent.getSerializableExtra(ARG1) as Delivery

        binding.delivery = mDelivery

    }

    companion object{
        private const val ARG1 = "delivery"

        fun newInstance(context: Context,delivery: Delivery): Intent {
            val intent = Intent(context,TrackDeliveryActivity::class.java)
            intent.putExtra(ARG1,delivery)
            return intent
        }
    }
}
