package com.example.deliveryapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.databinding.ActivityMainBinding
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
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


        binding.myDeliveriesRecyclerView.adapter = DeliveryPagingAdapter()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


    }

}
