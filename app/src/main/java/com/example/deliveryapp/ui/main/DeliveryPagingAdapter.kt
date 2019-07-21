package com.example.deliveryapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.AdapterDeliveryBinding
import com.example.deliveryapp.utils.BindablePagingAdapter
import java.lang.IllegalArgumentException

class DeliveryPagingAdapter : PagedListAdapter<Delivery,DeliveryPagingAdapter.DeliveryViewHolder>(DELIVERY_DIFF_CALLBACK), BindablePagingAdapter<Delivery>{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<AdapterDeliveryBinding>(
            layoutInflater, viewType, parent, false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bindDelivery(getItem(position)!!)
    }

    override fun submitItems(data: PagedList<Delivery>) {
        submitList(data)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.adapter_delivery
    }

    companion object{
        val DELIVERY_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Delivery>(){
            override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class DeliveryViewHolder(private val binding: AdapterDeliveryBinding): RecyclerView.ViewHolder(binding.root){


        fun bindDelivery(delivery: Delivery){
            binding.deliveryTitle.text = delivery.title

            val pointsPrefix = if(delivery.deliveryStatus == Delivery.STATUS_COMPLETED) "Delivered" else "Delivering"
            val points = "$pointsPrefix package from ${delivery.origin} to ${delivery.destination}"
            binding.deliveryPoints.text = points

            val statusText = when(delivery.deliveryStatus){
                Delivery.STATUS_COMPLETED -> "Delivered on ${delivery.deliveredAt}"
                Delivery.STATUS_PENDING -> "Delivery in progress"
                Delivery.STATUS_CANCELLED-> "Cancelled"
                else-> throw IllegalArgumentException("Unknown delivery status")
            }
            binding.deliveryStatusText.text = statusText

            @DrawableRes val statusIcon = when(delivery.deliveryStatus){
                Delivery.STATUS_COMPLETED -> R.drawable.ic_check
                Delivery.STATUS_PENDING -> R.drawable.ic_pending
                Delivery.STATUS_CANCELLED-> R.drawable.ic_error
                else-> throw IllegalArgumentException("Unknown delivery status")
            }
            binding.deliveryStatusImage.setImageResource(statusIcon)

            binding.executePendingBindings()
        }
    }
}