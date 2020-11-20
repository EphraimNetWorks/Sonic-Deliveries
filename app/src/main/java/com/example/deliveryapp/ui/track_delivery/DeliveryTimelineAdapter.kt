package com.example.deliveryapp.ui.track_delivery


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.AdapterDeliveryTimelineBinding
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.deliveryapp.R
import com.example.deliveryapp.utils.TimelineView


class DeliveryTimelineAdapter(private val delivery: Delivery) : RecyclerView.Adapter<DeliveryTimelineAdapter.TimeLineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<AdapterDeliveryTimelineBinding>(
            layoutInflater, R.layout.adapter_delivery_timeline, parent, false
        )
        return TimeLineViewHolder(binding, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.bindTimelineEvent(position)
    }

    inner class TimeLineViewHolder(private var binding: AdapterDeliveryTimelineBinding,
                                   viewType: Int) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.timeline.initLine(viewType)
        }

        fun bindTimelineEvent(position: Int){

            when(position){
                0-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_order_placed)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_placed)
                }
                1-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.package_in_transit)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_in_transit)
                }
                2-> {
                    if(delivery.deliveryStatus == Delivery.STATUS_CANCELLED){
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_cancelled)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_cancelled)
                    }else {
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_complete)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.idelivery_completed)
                    }
                }
            }

            @DrawableRes val timelineResMarker :Int
            @ColorRes val markerResColor:Int
            @ColorRes val textResColor:Int
            @ColorRes val lineResColor:Int

            when {
                delivery.deliveryStatus == Delivery.STATUS_CANCELLED -> {
                    timelineResMarker = R.drawable.ic_marker_complete
                    markerResColor = R.color.grey
                    textResColor = R.color.grey
                    lineResColor = R.color.grey

                }
                position < delivery.deliveryStatus -> {
                    timelineResMarker = R.drawable.ic_marker_complete
                    markerResColor = R.color.colorPrimary
                    textResColor = R.color.black
                    lineResColor = R.color.blue

                }
                position == delivery.deliveryStatus -> {
                    timelineResMarker = R.drawable.ic_marker_complete
                    markerResColor = R.color.colorAccent
                    textResColor = R.color.blue
                    lineResColor = R.color.colorAccent

                }
                else -> {
                    timelineResMarker = R.drawable.ic_marker_incomplete
                    markerResColor = R.color.grey
                    textResColor = R.color.grey
                    lineResColor = R.color.grey
                }
            }

            binding.timeline.setMarker(timelineResMarker,markerResColor)
            val textColor = ContextCompat.getColor(itemView.context,textResColor)
            val lineColor = ContextCompat.getColor(itemView.context,lineResColor)
            binding.deliveryStatusText.setTextColor(textColor)
            binding.timeline.setEndLineColor(lineColor)
            binding.timeline.setStartLineColor(lineColor)

            binding.executePendingBindings()
        }


    }
}
