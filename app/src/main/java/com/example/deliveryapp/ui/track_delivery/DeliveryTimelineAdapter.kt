package com.example.deliveryapp.ui.track_delivery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.AdapterDeliveryTimelineBinding
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.adapter_delivery_timeline.view.*
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.deliveryapp.R


class DeliveryTimelineAdapter(private val delivery: Delivery) : RecyclerView.Adapter<DeliveryTimelineAdapter.TimeLineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<AdapterDeliveryTimelineBinding>(
            layoutInflater, R.layout.adapter_delivery_timeline, parent, false
        )
        return TimeLineViewHolder(binding,viewType)
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
                                   private var viewType: Int) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.timeline.initLine(viewType)
        }

        fun bindTimelineEvent(position: Int){

            when(position){
                Delivery.STATUS_PLACED-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_order_placed)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.ic_check)
                }
                Delivery.STATUS_IN_TRANSIT-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.package_in_transit)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.ic_check)
                }
                3-> {
                    if(delivery.deliveryStatus == Delivery.STATUS_CANCELLED){
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_cancelled)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.ic_check)
                    }else {
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_complete)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.ic_cancelled)
                    }
                }
            }

            if(position <= delivery.deliveryStatus){
                setMarker(R.drawable.ic_marker_complete,R.color.colorPrimary)
                val color = itemView.context.resources.getColor(R.color.blue)
                binding.timeline.setEndLineColor(color,viewType)
                binding.timeline.setStartLineColor(color,viewType)
            }else{
                setMarker(R.drawable.ic_marker_incomplete,R.color.grey)
                val color = itemView.context.resources.getColor(R.color.grey)
                binding.timeline.setEndLineColor(color,viewType)
                binding.timeline.setStartLineColor(color,viewType)
            }
        }

        private fun setMarker(drawableResId: Int, colorFilter: Int) {
            binding.timeline.marker = VectorDrawableUtils.getDrawable(itemView.context, drawableResId, ContextCompat.getColor(itemView.context, colorFilter))
        }


    }
}

object VectorDrawableUtils {

    fun getDrawable(context: Context, drawableResId: Int): Drawable? {
        return VectorDrawableCompat.create(context.resources, drawableResId, context.theme)
    }

    fun getDrawable(context: Context, drawableResId: Int, colorFilter: Int): Drawable {
        val drawable = getDrawable(context, drawableResId)
        drawable!!.setColorFilter(ContextCompat.getColor(context, colorFilter), PorterDuff.Mode.SRC_IN)
        return drawable
    }

}