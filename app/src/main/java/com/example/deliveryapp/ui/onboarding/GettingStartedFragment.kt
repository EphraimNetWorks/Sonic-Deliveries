package com.example.deliveryapp.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.FragmentGetStartedBinding
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.signup.SignUpActivity


class GettingStartedFragment :Fragment(){

    lateinit var binding: FragmentGetStartedBinding
    private lateinit var mDelivery: Delivery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDelivery = arguments!!.getSerializable(ARG_PARAM1) as Delivery

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGetStartedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(context!!,SignUpActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(context!!,LoginActivity::class.java))
        }

    }

    companion object{
        private const val ARG_PARAM1 = "delivery"

        fun newInstance(delivery:Delivery):GettingStartedFragment{
            val frag = GettingStartedFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, delivery)
            frag.arguments = args
            return frag
        }
    }
}