package com.example.deliveryapp.login_signup

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.R
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.utils.CustomMatchers
import com.example.deliveryapp.utils.DataBindingIdlingResourceRule
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var testEmail:String
    private lateinit var testPassword:String
    private lateinit var testContext: Context

    private lateinit var observerIdlingResource:CountingIdlingResource

    @Before
    fun setUp(){
        activityRule.launchActivity(null)
        observerIdlingResource = CountingIdlingResource("Sign Up Observer")
        IdlingRegistry.getInstance().register(observerIdlingResource)
        activityRule.activity.idlingResource = observerIdlingResource

        testContext = getInstrumentation().context
    }

    @Test
    fun showEmptyAndInvalidEmailErrorMsg_onSignUpValidation(){


        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        onView(withId(R.id.login_email_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        testEmail = "1"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_phone_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPasswordErrorMsg_onSignUpValidation(){


        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        onView(withId(R.id.login_password_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        testPassword = "1"

        onView(withId(R.id.login_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_phone_error_message))))

    }

    @Test
    fun verifySignUpActivityStarted() {

        onView(withId(R.id.sign_up))
            .check(matches(isDisplayed()))
            .perform(click())

        // Verifies that the SignUpActivity received an intent
        // with the correct package name .

        intended(allOf(
            hasComponent(hasShortClassName(".SignUpActivity")),
            toPackage(SIGN_UP_PACKAGE_NAME)))

    }

    @Test
    fun goToMainActivity_afterLogin() {

        testEmail ="narteyephraim@gmail.com"
        testPassword = "asdfghjkl"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Verifies that the MainActivity received an intent
        // with the correct package name .

        intended(allOf(
            hasComponent(hasShortClassName(".MainActivity")),
            toPackage(MAIN_PACKAGE_NAME)))

    }



    companion object{

        private const val SIGN_UP_PACKAGE_NAME = "com.example.deliveryapp.ui.signup"
        private const val MAIN_PACKAGE_NAME = "com.example.deliveryapp.ui.main"
    }

}