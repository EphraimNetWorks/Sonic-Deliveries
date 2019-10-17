package com.example.deliveryapp.ui.user

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
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
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.*
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.MockitoAnnotations.*

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

    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var userDao: UserDao
    @Mock
    private lateinit var localDatabase: LocalDatabase

    private lateinit var userRepo: UserRepository

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<ApiCallback<User?>>

    @Before
    fun setUp(){
        initMocks(this)

        userRepo = UserRepository(apiService,userDao,localDatabase)

        TestAppInjector(userRepo, DeliveryRepository(
            Mockito.mock(ApiService::class.java),
            Mockito.mock(DeliveryDao::class.java))).newInject()

        testContext = getInstrumentation().targetContext

        activityRule.launchActivity(Intent(testContext,SignUpActivity::class.java))
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

        testName = "King Julian"

        onView(withId(R.id.signup_name_editext))
            .perform(typeText(testName), closeSoftKeyboard())

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

        testName = "King Julian"
        testPhone = "+23399323499"

        onView(withId(R.id.signup_name_editext))
            .perform(typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(testPhone), closeSoftKeyboard())

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

        testName = "King Julian"
        testPhone = "+23399323499"
        testEmail = "worldwideking@gmail.com"

        onView(withId(R.id.signup_name_editext))
            .perform(typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_confirm_password_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.signup_password_editext))
            .perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

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
            .perform(typeText(testName), closeSoftKeyboard())

        onView(withId(R.id.signup_phone_editext))
            .perform(typeText(testPhone), closeSoftKeyboard())

        onView(withId(R.id.signup_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())
        onView(withId(R.id.signup_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.signup_button))
            .perform(click())

        onView(withId(R.id.signup_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

        onView(withId(R.id.signup_confirm_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.password_dont_match_error_message))))

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
    fun backButtonClosesActivity(){

        Espresso.pressBackUnconditionally()

        assertTrue(activityRule.activity.isDestroyed)
    }

    @Test
    fun goToLoginActvity_afterSigningUp(){
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

        Mockito.verify(apiService).signUpUser(ArgumentMatchers.any(SignUpRequest::class.java), callbackCaptor.capture())

        val callback = callbackCaptor.value
        callback.onSuccess(User().apply {
            email = testEmail
            id = "1"
            name = "Ephraim Nartey"

        })

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

        val errMsg = "error"
        Mockito.verify(apiService).signUpUser(ArgumentMatchers.any(SignUpRequest::class.java), callbackCaptor.capture())

        val callback = callbackCaptor.value
        callback.onFailed(errMsg)

        onView(withId(R.id.signup_error_textview))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(withText(errMsg)))


    }

}