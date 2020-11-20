package com.example.deliveryapp.ui.user

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.deliveryapp.*
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.modules.ApiServiceModule
import com.example.deliveryapp.di.modules.AppModule
import com.example.deliveryapp.di.modules.DeliveryModule
import com.example.deliveryapp.di.modules.UserModule
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.utils.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.whenever
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject


@UninstallModules(UserModule::class, DeliveryModule::class, ApiServiceModule::class)
@HiltAndroidTest
class LoginActivityTest {


    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java,false,false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(intentsTestRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    @BindValue
    @Mock
    lateinit var apiService: ApiService

    @BindValue
    @Mock
    lateinit var deliveryDao: DeliveryDao

    @BindValue
    @Mock
    lateinit var userDao: UserDao

    private lateinit var testEmail:String
    private lateinit var testPassword:String
    private lateinit var testContext: Context

    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        testContext = getInstrumentation().targetContext

        intentsTestRule.launchActivity(Intent(testContext,LoginActivity::class.java))

        EspressoTestUtil.disableProgressBarAnimations(intentsTestRule)

    }

    @Test
    fun showEmptyAndInvalidEmailErrorMsg_onSignUpValidation(){


        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        onView(withId(R.id.login_email_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_email_field_error))))

        testEmail = "1"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_email_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_email_error_message))))

    }

    @Test
    fun showEmptyAndInvalidPasswordErrorMsg_onSignUpValidation(){

        val testEmail = "narteyephraim@gmail.com"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        onView(withId(R.id.login_password_editext))
            .perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.empty_password_field_error_message))))

        testPassword = "1"

        onView(withId(R.id.login_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.login_button))
            .perform(click())

        onView(withId(R.id.login_password_text_layout))
            .check(matches(CustomMatchers.withError(testContext.getString(R.string.invalid_password_error_message))))

    }

    @Test
    fun verifySignUpActivityStarted() {

        onView(withId(R.id.sign_up))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(scrollTo(),click())

        // Verifies that the SignUpActivity received an intent
        // with the correct package name .

        intended(allOf(
            hasComponent(hasShortClassName(Constants.SIGNUP_ACTIVITY_SHORT_CLASS_NAME)),
            toPackage(Constants.PACKAGE_NAME)))

    }

    @Test
    fun goToMainActivity_afterLogin() {

        whenever(userDao.getCurrentUser()).thenReturn( MutableLiveData(User().apply {
            email = ""
            id = "1"
            name = "Ephraim Nartey"

        }))
        val dSFactory:DataSource.Factory<Int,Delivery> = MockRoomDataSource.mockDataSourceFactory(
            listOf())
        whenever(deliveryDao.getDeliveriesInTransit()).thenReturn( dSFactory)
        whenever(deliveryDao.getDeliveriesPlaced()).thenReturn(dSFactory)
        whenever(deliveryDao.getCompletedDeliveries()).thenReturn( dSFactory)

        testEmail ="narteyephraim@gmail.com"
        testPassword = "asdfghjkl"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        doAnswer {
            val callback = it.arguments[2] as ApiCallback<User>
            callback.onSuccess(User().apply {
                email = testEmail
                id = "1"
                name = "Ephraim Nartey"

            })

            return@doAnswer

        }.`when`(apiService).loginUser(anyString(), anyString(), any())

        onView(withId(R.id.login_button))
            .check(matches(isDisplayed()))
            .perform(click())


        // Verifies that the MainActivity received an intent
        // with the correct package name .
        intended(allOf(
            hasComponent(hasShortClassName(Constants.MAIN_ACTIVITY_SHORT_CLASS_NAME)),
            toPackage(Constants.PACKAGE_NAME)))

    }

    @Test
    fun showError_onLoginError() {

        testEmail ="narteyephraim@gmail.com"
        testPassword = "asdfghjkl"

        onView(withId(R.id.login_email_editext))
            .perform(typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.login_password_editext))
            .perform(typeText(testPassword), closeSoftKeyboard())

        val errMsg = "error"
        doAnswer {
            val callback = it.arguments[2] as ApiCallback<User>
            callback.onFailed(errMsg)

            return@doAnswer

        }.`when`(apiService).loginUser(anyString(), anyString(), any())

        onView(withId(R.id.login_button))
            .check(matches(isDisplayed()))
            .perform(click())


        onView(withId(R.id.login_error_textview))
            .check(matches(withText(errMsg)))

    }

}