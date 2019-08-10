package com.example.deliveryapp.ui.user

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.R
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.utils.*
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpActivityTest {
    @get:Rule
    var activityRule: ActivityTestRule<SignUpActivity>
            = ActivityTestRule(SignUpActivity::class.java)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    private lateinit var testName:String
    private lateinit var testPhone:String
    private lateinit var testEmail:String
    private lateinit var testPassword:String
    private lateinit var testConfirmPassword:String

    private lateinit var testContext: Context

    @Before
    fun setUp(){
        activityRule.launchActivity(null)

        testContext = getInstrumentation().context
    }

    @Test
    fun showEmptyNameErrorMsg_onSignUpValidation(){


        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_name_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_name_error_message))))

        testName = ""

        onView(withId(R.id.signup_name_editext))
            .perform(typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_name_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_name_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPhoneErrorMsg_onSignUpValidation(){


        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        testPhone = "1"

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_phone_error_message))))

    }

    @Test
    fun showEmptyAndInvalidEmailErrorMsg_onSignUpValidation(){


        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        onView(withId(R.id.signup_email_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        onView(withId(R.id.signup_email_editext))
            .perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_email_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPasswordErrorMsg_onSignUpValidation(){

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_password_editext))
            .perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_password_error_message))))

    }

    @Test
    fun showEmptyAndInvalidConfirmPasswordErrorMsg_onSignUpValidation(){


        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_password_error_message))))

    }

    @Test
    fun showPassDontMatchErrorMsg_onSignUpValidation(){

        testPassword = "password"
        testConfirmPassword = "testconfirm"

        onView(withId(R.id.signup_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText(testConfirmPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

    }

    @Test
    fun goToMainActvity_afterSigningUp(){
        testName = "Ephraim Nartey"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"
        testPassword = "myloyalsubjects"
        testConfirmPassword = "myloyalsubjects"


        onView(withId(R.id.signup_name_editext))
            .perform(typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.signup_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText(testConfirmPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.MAIN_ACTIVITY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME)
            )
        )

    }

}