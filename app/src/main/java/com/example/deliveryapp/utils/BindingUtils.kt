package com.example.deliveryapp.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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


class BindingUtils {

    @BindingAdapter("data")
    fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T) {
        if (recyclerView.adapter is BindableAdapter<*>) {
            (recyclerView.adapter as BindableAdapter<T>).submitItems(data)
        }
    }

    @BindingAdapter("data")
    fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: PagedList<T>) {
        if (recyclerView.adapter is BindablePagingAdapter<*>) {
            (recyclerView.adapter as BindablePagingAdapter<T>).submitItems(data)
        }
    }

    @BindingAdapter("android:src")
    fun setImageResource(imageView: ImageView, url: String) {
        Glide.with(imageView)
            .load(url)
            .apply(RequestOptions().placeholder(R.drawable.ic_account_circle).diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(imageView)
    }

    @BindingAdapter("android:visibility")
    fun setShimmerVisibility(shimmerFrameLayout: ShimmerFrameLayout, dataList: Any?) {
        when (dataList) {
            null -> shimmerFrameLayout.visibility = View.VISIBLE
            is Iterable<*> -> shimmerFrameLayout.visibility = if (dataList.toList().isNullOrEmpty()) View.VISIBLE else View.GONE
            else -> shimmerFrameLayout.visibility = View.GONE
        }
    }

    @BindingAdapter("networkStatus")
    fun setNetworkState(progressBar: ProgressBar, networkStatus: Status) {
        progressBar.visibility = if(networkStatus == Status.RUNNING) View.VISIBLE else View.GONE
    }

    @BindingAdapter("networkStatus")
    fun setNetworkState(retryButton: ImageView, networkStatus: Status) {
        retryButton.visibility = if(networkStatus == Status.FAILED) View.VISIBLE else View.GONE
    }

    @BindingAdapter("networkStatus")
    fun setNetworkState(retryButton: Button, networkStatus: Status) {
        retryButton.visibility = if(networkStatus == Status.FAILED) View.VISIBLE else View.GONE
    }

    @BindingAdapter("networkStatus")
    fun setNetworkState(errorTextview: TextView, networkStatus: Status) {
        errorTextview.visibility = if(networkStatus == Status.FAILED) View.VISIBLE else View.GONE
    }
}