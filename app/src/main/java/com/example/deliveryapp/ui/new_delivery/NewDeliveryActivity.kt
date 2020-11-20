package com.example.deliveryapp.ui.new_delivery

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.databinding.ActivityNewDeliveryBinding
import dagger.android.AndroidInjection
import dagger.hilt.android.AndroidEntryPoint
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status

@AndroidEntryPoint
class NewDeliveryActivity : AppCompatActivity(),DeliveryFormValidation{

    private lateinit var binding : ActivityNewDeliveryBinding
    private lateinit var formFragment: NewDeliveryFormFragment

    private val viewModel by viewModels<NewDeliveryViewModel>()

    private lateinit var mDelivery:Delivery

    private lateinit var currentFragment:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if (savedInstanceState != null) {
            //Restore the fragment's instance
            formFragment = supportFragmentManager.getFragment(savedInstanceState, FORM_FRAGMENT_TAG) as NewDeliveryFormFragment
        }else{
            initNewDeliveryForms()
        }
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_delivery)

        setUpActionBar()

        binding.lifecycleOwner = this

        setUpFormFragment()

        binding.nextButton.setOnClickListener { onNextClicked() }

        binding.backButton.setOnClickListener { onBackClicked() }
    }

    private fun setUpActionBar(){
        binding.toolbar.title = getString(R.string.new_delivery)
        binding.toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun onNextClicked(){
        if(currentFragment == FORM_FRAGMENT_TAG){
            formFragment.validateNewDelivery()
        }else if(currentFragment == SUMMARY_FRAGMENT_TAG){
            viewModel.submitNewDelivery(mDelivery)
            viewModel.networkState.observe(this, {
                binding.networkState = it
                handleNetworkState(it)})
        }
    }

    private fun handleNetworkState(networkState: NetworkState){

        if(networkState.status == Status.SUCCESS){
            finish()
            Toast.makeText(this,getString(R.string.new_delivery_success),Toast.LENGTH_LONG).show()
        }
    }

    private fun onBackClicked(){
        if(currentFragment == SUMMARY_FRAGMENT_TAG){
            setUpFormFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        supportFragmentManager.putFragment(outState, FORM_FRAGMENT_TAG, formFragment)
    }

    private fun initNewDeliveryForms(){
        formFragment = NewDeliveryFormFragment.newInstance()
    }

    private fun setUpFormFragment(){
        // update the main content by replacing fragments
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
            android.R.anim.slide_out_right)
        fragmentTransaction.replace(binding.formFrame.id, formFragment, FORM_FRAGMENT_TAG)
        fragmentTransaction.commitAllowingStateLoss()


        currentFragment = FORM_FRAGMENT_TAG
        updateButtons()
    }

    fun setUpSummaryFragment(delivery: Delivery){
        val summaryFragment = NewDeliverySummaryFragment.newInstance(delivery)
        // update the main content by replacing fragments
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
            android.R.anim.slide_out_right)
        fragmentTransaction.replace(binding.formFrame.id, summaryFragment, SUMMARY_FRAGMENT_TAG)
        fragmentTransaction.commitAllowingStateLoss()

        currentFragment = SUMMARY_FRAGMENT_TAG
        updateButtons()
    }

    private fun updateButtons(){
        if(currentFragment == FORM_FRAGMENT_TAG){
            binding.nextButton.text = getString(R.string.next)
            binding.backButton.setBackgroundResource(R.drawable.cancel_button_background)
            binding.backButton.setTextColor(Color.GRAY)
        }else if(currentFragment == SUMMARY_FRAGMENT_TAG){
            binding.nextButton.text = getString(R.string.submit)
            binding.backButton.setBackgroundResource(R.drawable.color_button_background)
            binding.backButton.setTextColor(Color.WHITE)
        }
    }

    override fun onValidationSuccess() {

        mDelivery = formFragment.getNewDelivery()
        setUpSummaryFragment(mDelivery)
    }

    companion object{

        const val FORM_FRAGMENT_TAG = "formFragment"
        const val SUMMARY_FRAGMENT_TAG = "summaryFragment"

        fun newInstance(context: Context): Intent {
            return Intent(context, NewDeliveryActivity::class.java)
        }
    }
}
