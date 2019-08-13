package com.example.deliveryapp.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AsyncUtils {
    interface AsyncCallback<T> {
        fun onComplete(result: T)
        fun onException(e: Exception?)
    }

    suspend fun <T> awaitCallback(block: (AsyncCallback<T>) -> Unit): T =
        suspendCancellableCoroutine { cont ->
            block(object : AsyncCallback<T> {
                override fun onComplete(result: T) = cont.resume(result)
                override fun onException(e: Exception?) {
                    e?.let { cont.resumeWithException(it) }
                }
            })
        }
}