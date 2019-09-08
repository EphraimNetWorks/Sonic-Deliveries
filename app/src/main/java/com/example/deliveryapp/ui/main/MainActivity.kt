package com.example.deliveryapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.ActivityMainBinding
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.utils.ViewModelFactory
import com.google.gson.Gson
import dagger.android.AndroidInjection
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(MainViewModel::class.java)

        setUpSalutation()

        viewModel.initMyDeliveries()

        binding.placedDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.inTransitDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.completedDeliveriesRecyclerView.adapter = DeliveryPagingAdapter(this)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.newDeliveryFab.setOnClickListener {
            startActivity(NewDeliveryActivity.newInstance(this))
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logoutUser()
            startActivity(LoginActivity.newInstance(this, SALUTATION_TYPE_NEW_LOGIN))
        }

    }

    private fun startObservers(){
        viewModel.mostRecentDelivery.observe(this, Observer {
            if(it!=null) binding.summaryTextview.text = getSummaryMessage(it)
        })

    }

    private fun removeObservers(){
        viewModel.mostRecentDelivery.removeObservers(this)
    }

    private fun setUpSalutation(){
        val salutationType = intent.getIntExtra(EXTRA_SALUTATION_TYPE,1)
        binding.salutationTextview.text = getSalutationMessage(salutationType)
    }

    private fun getSalutationMessage(salutationType: Int):String{
        return when(salutationType){
            SALUTATION_TYPE_SIGN_UP -> getString(R.string.new_user_salutation)
            SALUTATION_TYPE_NEW_LOGIN -> getString(R.string.new_login_salutation)
            SALUTATION_TYPE_ALREADY_LOGGED_IN -> viewModel.getRandomItemFromList(
                ArrayList<String>().apply {
                    addAll(resources.getStringArray(R.array.old_user_salutations))
                })
            else-> throw IllegalArgumentException("Unknown salutation type")
        }
    }

    private fun getSummaryMessage(recentDelivery: Delivery):String{

        Timber.d("recent delivery: ${Gson().toJson(recentDelivery)} comp: ${viewModel.completedDeliveries?.value?.size}")
        return when(recentDelivery.deliveryStatus){
            Delivery.STATUS_PLACED -> "${getString(R.string.placed_summary_message_prefix)} ${recentDelivery.title}" +
                    " ${getString(R.string.placed_summary_message_suffix)} ${recentDelivery.pickUpTimeDate!!.getDateFormat1()}"
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {

        const val EXTRA_SALUTATION_TYPE = "salutationType"

        const val SALUTATION_TYPE_SIGN_UP = 0

        const val SALUTATION_TYPE_NEW_LOGIN = 1

        const val SALUTATION_TYPE_ALREADY_LOGGED_IN = 2

        fun newInstance(context: Context, salutationType:Int): Intent {
            return Intent(context,MainActivity::class.java).apply {
                putExtra(EXTRA_SALUTATION_TYPE, salutationType)
            }
        }
    }

}
