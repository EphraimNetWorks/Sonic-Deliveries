package com.example.deliveryapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.AdapterCompletedBinding
import com.example.deliveryapp.databinding.AdapterDeliveryInTransitBinding
import com.example.deliveryapp.databinding.AdapterDeliveryPlacedBinding
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.BindablePagingAdapter
import java.lang.IllegalArgumentException

class DeliveryPagingAdapter(val activity: MainActivity) : PagedListAdapter<Delivery,DeliveryPagingAdapter.DeliveryViewHolder>(DELIVERY_DIFF_CALLBACK),
    BindablePagingAdapter<Delivery>{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {

        val deliveryItem = getItem(position)!!
        return when(deliveryItem.deliveryStatus){
            Delivery.STATUS_PLACED -> holder.bindPlacedDelivery(deliveryItem)
            Delivery.STATUS_IN_TRANSIT -> holder.bindInTransitDelivery(deliveryItem)
            Delivery.STATUS_COMPLETED -> holder.bindCompletedDelivery(deliveryItem)
            Delivery.STATUS_CANCELLED -> holder.bindCancelledDelivery(deliveryItem)
            else -> throw IllegalArgumentException("unknown argument type")
        }
    }

    override fun submitItems(data: PagedList<Delivery>) {
        submitList(data)
    }

    override fun getItemViewType(position: Int): Int {
        val deliveryItem = getItem(position)!!
        return when(deliveryItem.deliveryStatus){
            Delivery.STATUS_PLACED -> R.layout.adapter_delivery_placed
            Delivery.STATUS_IN_TRANSIT -> R.layout.adapter_delivery_in_transit
            Delivery.STATUS_COMPLETED -> R.layout.adapter_completed
            Delivery.STATUS_CANCELLED -> R.layout.adapter_completed
            else -> throw IllegalArgumentException("unknown argument type")
        }
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

    inner class DeliveryViewHolder(private val genericBinding: ViewDataBinding): RecyclerView.ViewHolder(genericBinding.root),View.OnClickListener{

        private lateinit var delivery:Delivery

        fun bindInTransitDelivery(delivery: Delivery){
            this.delivery = delivery

            val binding = genericBinding as AdapterDeliveryInTransitBinding
            binding.deliveryItemName.text = delivery.title

            val originString = "${itemView.context.getString(R.string.delivering_package_from)} ${delivery.pickUpAddress}"
            binding.deliveryOrigin.text = originString

            binding.deliveryEta.text = "${delivery.estimatedTimeOfArrival}"

            binding.executePendingBindings()
        }

        fun bindPlacedDelivery(delivery: Delivery){
            this.delivery = delivery

            val binding = genericBinding as AdapterDeliveryPlacedBinding
            binding.deliveryItemName.text = delivery.title

            val originString = "${itemView.context.getString(R.string.delivering_package_from)} ${delivery.pickUpAddress}"
            binding.deliveryOrigin.text = originString

            binding.pickUpDate.text = "${delivery.pickUpTime}"

            binding.executePendingBindings()
        }

        fun bindCompletedDelivery(delivery: Delivery){
            this.delivery = delivery

            val binding = genericBinding as AdapterCompletedBinding
            binding.deliveryItemName.text = delivery.title

            val originString = "${itemView.context.getString(R.string.delivering_package_from)} ${delivery.pickUpAddress}"
            binding.deliveryOrigin.text = originString

            val dateDeliveredString = "${itemView.context.getString(R.string.delivered_on)} ${delivery.estimatedTimeOfArrival}"
            binding.dateDelivered.text = dateDeliveredString

            binding.deliveryStatusText.text = itemView.context.getString(R.string.delivered)

            binding.deliveryStatusText.setTextColor(itemView.context.resources.getColor(R.color.green))

            binding.deliveryStatusIcon.setImageResource(R.drawable.ic_check)

            binding.executePendingBindings()
        }

        fun bindCancelledDelivery(delivery: Delivery){
            this.delivery = delivery

            val binding = genericBinding as AdapterCompletedBinding
            binding.deliveryItemName.text = delivery.title

            val originString = "${itemView.context.getString(R.string.delivering_package_from)} ${delivery.pickUpAddress}"
            binding.deliveryOrigin.text = originString

            val dateDeliveredString = "${itemView.context.getString(R.string.delivered_on)} ${delivery.estimatedTimeOfArrival}"
            binding.dateDelivered.text = dateDeliveredString

            binding.deliveryStatusText.text = itemView.context.getString(R.string.cancelled)

            binding.deliveryStatusText.setTextColor(itemView.context.resources.getColor(R.color.grey))

            binding.deliveryStatusIcon.setImageResource(R.drawable.ic_cancelled)

            binding.executePendingBindings()
        }

        override fun onClick(p0: View?) {
            var options = when(delivery.deliveryStatus){
                Delivery.STATUS_PLACED-> {
                    val binding = genericBinding as AdapterDeliveryPlacedBinding
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,binding.deliveryItemName,"itemName")
                }
                Delivery.STATUS_IN_TRANSIT-> {
                    val binding = genericBinding as AdapterDeliveryInTransitBinding
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,binding.deliveryItemName,"itemName")
                }
                Delivery.STATUS_COMPLETED-> {
                    val binding = genericBinding as AdapterCompletedBinding
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,binding.deliveryItemName,"itemName")
                }
                Delivery.STATUS_CANCELLED-> {
                    val binding = genericBinding as AdapterCompletedBinding
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity,binding.deliveryItemName,"itemName")
                }
                else -> throw IllegalArgumentException("unknown delivery status")
            }
            itemView.context.startActivity(TrackDeliveryActivity.newInstance(itemView.context,delivery),options.toBundle())

        }
    }
}