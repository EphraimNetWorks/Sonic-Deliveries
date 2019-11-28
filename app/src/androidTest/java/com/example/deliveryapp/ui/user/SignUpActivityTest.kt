package com.example.deliveryapp.ui.user

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.utils.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpActivityTest {
    @get:Rule
    var activityRule: IntentsTestRule<SignUpActivity>
            = IntentsTestRule(SignUpActivity::class.java,true,false)

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

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var localDatabase: LocalDatabase

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<ApiCallback<User?>>

    @Before
    fun setUp(){

        TestAppInjector.inject{it.inject(this)}

        testContext = getInstrumentation().targetContext

        activityRule.launchActivity(Intent(testContext,SignUpActivity::class.java))
    }

    @Test
    fun showEmptyNameErrorMsg_onSignUpValidation(){

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_name_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_name_error_message))))

        testName = ""

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_name_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_name_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPhoneErrorMsg_onSignUpValidation(){

        testName = "King Julian"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_phone_error_message))))

        testPhone = "1"

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_phone_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_phone_error_message))))

    }

    @Test
    fun showEmptyAndInvalidEmailErrorMsg_onSignUpValidation(){

        testName = "King Julian"
        testPhone = "+23399323499"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_email_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPasswordErrorMsg_onSignUpValidation(){

        testName = "King Julian"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(scrollTo(),typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_password_editext))
            .perform(scrollTo(),typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_password_error_message))))

    }

    @Test
    fun showPassDontMatchErrorMsg_onSignUpValidation(){

        testName = "King Julian"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"
        testPassword = "password"
        testConfirmPassword = "testconfirm"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText(testEmail), closeSoftKeyboard())
        onView(withId(R.id.signup_password_editext))
            .perform(scrollTo(),typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(scrollTo(),typeText(testConfirmPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

    }

    @Test
    fun goToLoginActvity_afterSigningUp(){
        testName = "Ephraim Nartey"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"
        testPassword = "myloyalsubjects"
        testConfirmPassword = "myloyalsubjects"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.signup_password_editext))
            .perform(scrollTo(),typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(scrollTo(),typeText(testConfirmPassword), closeSoftKeyboard())

        doAnswer {
            val callback = it.arguments[1] as ApiCallback<User>
            callback.onSuccess(User().apply {
                email = testEmail
                id = "1"
                name = "Ephraim Nartey"

            })

            return@doAnswer

        }.`when`(apiService).signUpUser(any(), any())

        onView(withId(R.id.signup_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .perform(scrollTo(),click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.LOGIN_ACTIVITY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME)
            )
        )

    }
    @Test
    fun showNetworkError_afterSigningUp(){
        testName = "Ephraim Nartey"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"
        testPassword = "myloyalsubjects"
        testConfirmPassword = "myloyalsubjects"

        onView(withId(R.id.signup_name_editext))
            .perform(scrollTo(),typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(scrollTo(),typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(scrollTo(),typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.signup_password_editext))
            .perform(scrollTo(),typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(scrollTo(),typeText(testConfirmPassword), closeSoftKeyboard())

        val errMsg = "error"
        doAnswer {
            val callback = it.arguments[1] as ApiCallback<User>
            callback.onFailed(errMsg)

            return@doAnswer

        }.`when`(apiService).signUpUser(any(), any())

        onView(withId(R.id.signup_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .perform(scrollTo(),click())

        onView(withId(R.id.signup_error_textview))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(withText(errMsg)))


    }

}