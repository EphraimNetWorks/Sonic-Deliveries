package com.example.deliveryapp.ui.new_delivery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.deliveryapp.R
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity

class NewDeliveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)
    }

    companion object{

        fun newInstance(context: Context): Intent {
            return Intent(context, TrackDeliveryActivity::class.java)
        }
    }
}
