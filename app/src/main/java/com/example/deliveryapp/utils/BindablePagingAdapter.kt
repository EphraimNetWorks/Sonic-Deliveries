package com.example.deliveryapp.utils

import androidx.paging.PagedList

interface BindablePagingAdapter<T> {
    fun submitItems(data: PagedList<T>)
}