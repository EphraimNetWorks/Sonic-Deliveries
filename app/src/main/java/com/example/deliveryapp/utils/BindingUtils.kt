package com.example.deliveryapp.utils

import android.view.View
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.deliveryapp.R
import com.facebook.shimmer.ShimmerFrameLayout
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status


object BindingUtils {

    @JvmStatic
    @BindingAdapter("data")
    fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: PagedList<T>?) {
        if(data!=null) {
            if (recyclerView.adapter is BindablePagingAdapter<*>) {
                (recyclerView.adapter as BindablePagingAdapter<T>).submitItems(data)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(imageView: ImageView, url: String?) {
        Glide.with(imageView)
            .load(url?:"")
            .apply(RequestOptions().placeholder(R.drawable.ic_account_circle).diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setShimmerVisibility(shimmerFrameLayout: ShimmerFrameLayout, dataList:PagedList<*>?) {
        when (dataList) {
            null -> shimmerFrameLayout.visibility = View.VISIBLE
            else -> shimmerFrameLayout.visibility =
                if (dataList.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setShimmerNetworkVisibility(shimmerFrameLayout: ShimmerFrameLayout, networkStatus: Status?) {
        when (networkStatus) {
            null -> shimmerFrameLayout.visibility = View.VISIBLE
            else -> shimmerFrameLayout.visibility =
                if (networkStatus == Status.RUNNING) View.VISIBLE else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setNetworkState(progressBar: ProgressBar, networkStatus: Status?) {

        progressBar.visibility = if(networkStatus == null) View.GONE else if (networkStatus == Status.RUNNING) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setNetworkState(retryButton: ImageView, networkStatus: Status?) {
        retryButton.visibility = if(networkStatus == null) View.GONE else if (networkStatus == Status.FAILED) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setNetworkState(frameLayout: FrameLayout, networkStatus: Status?) {
        frameLayout.visibility = if(networkStatus == null) View.VISIBLE else if (networkStatus == Status.SUCCESS) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setNetworkState(errorTextview: TextView, networkStatus: Status?) {
        errorTextview.visibility = if(networkStatus == null) View.INVISIBLE else if (networkStatus == Status.FAILED) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("networkStatus")
    fun setNetworkState(editText: EditText, networkStatus: Status?) {
        editText.isClickable = if(networkStatus == null) true
        else networkStatus == Status.RUNNING
    }



    @JvmStatic
    @BindingAdapter("listCount")
    fun listCount(noItemsTextview: TextView, size: String) {
        noItemsTextview.visibility = if(size.toInt()>0) View.GONE else View.VISIBLE
    }
}