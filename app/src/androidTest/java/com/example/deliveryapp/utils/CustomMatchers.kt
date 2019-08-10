package com.example.deliveryapp.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
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
    fun withTitle(title: String?): Matcher<View> {
        return object : BoundedMatcher<View, Toolbar>(Toolbar::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("Checking the matcher on received view: ")
                description.appendText("with title= $title")
            }

            override fun matchesSafely(foundView: Toolbar): Boolean {
                return foundView.title == title
            }
        }
    }
    fun withTextInList(strings: List<String>): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("Checking the matcher on received view: ")
                description.appendText("with expectedError= $strings")
            }

            override fun matchesSafely(foundView: TextView): Boolean {
                var isTextInList = false
                for(string in strings){
                    if(string == foundView.text){
                        isTextInList = true
                    }
                }
                return isTextInList
            }
        }
    }
}