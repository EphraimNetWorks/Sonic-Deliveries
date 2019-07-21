package com.example.deliveryapp.data.remote

import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status

class NetworkState {

    var status: Status? = null
        private set

    var message: String? = null

    var taskName: String? = null

    private constructor(status: Status, message: String) {
        this.status = status
        this.message = message
    }

    private constructor(status: Status) {
        this.status = status
    }

    companion object {

        var LOADED = NetworkState(Status.SUCCESS)

        var LOADING = NetworkState(Status.RUNNING)

        fun error(message: String?): NetworkState {
            return NetworkState(Status.FAILED, message ?: "unknown error")
        }
    }
}

