package com.example.deliveryapp.ui.new_delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.FragmentNewDeliverySummaryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewDeliverySummaryFragment :Fragment(){

    lateinit var binding: FragmentNewDeliverySummaryBinding
    private lateinit var mDelivery: Delivery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mDelivery = it.getSerializable(ARG_PARAM1) as Delivery

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewDeliverySummaryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.delivery = mDelivery

    }

    companion object{
        private const val ARG_PARAM1 = "delivery"

        fun newInstance(delivery:Delivery):NewDeliverySummaryFragment{
            val frag = NewDeliverySummaryFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, delivery)
            frag.arguments = args
            return frag
        }
    }
}