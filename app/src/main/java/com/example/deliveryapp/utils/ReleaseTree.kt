package com.example.deliveryapp.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree(){
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        FirebaseCrashlytics.getInstance().log(message)
    }
}