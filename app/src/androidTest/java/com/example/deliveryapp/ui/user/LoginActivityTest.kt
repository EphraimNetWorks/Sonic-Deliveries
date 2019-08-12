package com.example.deliveryapp.ui.user

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.*
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.di.FakeDeliveryRepository
import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.utils.*
import junit.framework.Assert
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import timber.log.Timber
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java,false,false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(intentsTestRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    @Mock
    private lateinit var userRepo: UserRepository
    @Inject
    lateinit var injected: UserRepository

    private lateinit var testEmail:String
    private lateinit var testPassword:String
    private lateinit var testContext: Context
    private lateinit var injector: TestAppInjector

    private val testProvider = DispatcherProvider(
        IO = Dispatchers.Unconfined,
        Main = Dispatchers.Unconfined
    )

    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        //injector = TestAppInjector(TestMainModule(userRepo,FakeDeliveryRepository()))

        injector = TestAppInjector(userRepo,FakeDeliveryRepository())

        injector.newInject()

        testContext = getInstrumentation().targetContext

        intentsTestRule.launchActivity(Intent(testContext,LoginActivity::class.java))

        EspressoTestUtil.disableProgressBarAnimations(intentsTestRule)

        Mockito.`when`(userRepo.getCurrentUser()).thenReturn (MutableLiveData())

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
            .perform(click())

        // Verifies that the SignUpActivity received an intent
        // with the correct package name .

        intended(allOf(
            hasComponent(hasShortClassName(Constants.SIGNUP_ACTIVITY_SHORT_CLASS_NAME)),
            toPackage(Constants.PACKAGE_NAME)))

    }



    @Test
    fun backButtonClosesActivity(){

        EspressoTestUtil.disableProgressBarAnimations(intentsTestRule)

        Espresso.pressBackUnconditionally()

        Assert.assertTrue(intentsTestRule.activity.isDestroyed)
    }

    @Test
    fun goToMainActivity_afterLogin() {

        testEmail ="narteyephraim@gmail.com"
        testPassword = "asdfghjkl"

        val networkLoaded = MutableLiveData<NetworkState>()
        networkLoaded.postValue(NetworkState.LOADED)
        Mockito.`when`(userRepo.getNetworkState()).thenReturn(networkLoaded)

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
            hasComponent(hasShortClassName(Constants.MAIN_ACTIVITY_SHORT_CLASS_NAME)),
            toPackage(Constants.PACKAGE_NAME)))

    }


}