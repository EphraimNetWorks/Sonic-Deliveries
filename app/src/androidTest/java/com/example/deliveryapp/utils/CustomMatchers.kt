package com.example.deliveryapp.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Matcher
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description


object CustomMatchers{
    fun withError(expectedError: String?): Matcher<View> {
        return object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("Checking the matcher on received view: ")
                description.appendText("with expectedError= $expectedError")
            }

            override fun matchesSafely(foundView: TextInputLayout): Boolean {
                return foundView.error == expectedError
            }
        }
    }
}