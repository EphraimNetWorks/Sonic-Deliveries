package com.example.deliveryapp

import java.util.regex.Pattern

object TestPatterns {

    private const val EMAIL_PATTERN =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private const val PHONE_NUMBER_PATTERN = "^\\+[1-9]{1}[0-9]{3,14}\$"

    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(EMAIL_PATTERN)
    private val PHONE_PATTERN = Pattern.compile(PHONE_NUMBER_PATTERN)
}