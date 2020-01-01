package com.example.deliveryapp.utils

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree(){
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.log(priority, tag, message)
    }
}