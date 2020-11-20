package com.example.deliveryapp.ui.user

import android.content.Intent
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
import com.example.deliveryapp.di.modules.ApiServiceModule
import com.example.deliveryapp.di.modules.AppModule
import com.example.deliveryapp.di.modules.DeliveryModule
import com.example.deliveryapp.di.modules.UserModule
import com.example.deliveryapp.ui.onboarding.OnBoardingActivity
import com.example.deliveryapp.utils.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
class OnBoardingActivityTest {


    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val intentsTestRule = IntentsTestRule(OnBoardingActivity::class.java,false,false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(intentsTestRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()


    @Before
    fun setUp(){

        val testContext = getInstrumentation().targetContext

        intentsTestRule.launchActivity(Intent(testContext,OnBoardingActivity::class.java))

    }

    @Test
    fun verifyOnBoardingPageContents() {

        val firstPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page1_title)
        val firstPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page1_title)

        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(firstPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(firstPageDescription))))

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        val secondPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page2_title)
        val secondPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page2_title)

        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(secondPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(secondPageDescription))))

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        val thirdPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page3_title)
        val thirdPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page3_title)


        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(thirdPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(thirdPageDescription))))

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        val getStartedPageTitle = intentsTestRule.activity.getString(R.string.getting_started)
        val getStartedDescription = intentsTestRule.activity.getString(R.string.getting_started_message)


        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(getStartedPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(getStartedDescription))))


    }


    @Test
    fun verifyOnBoardingPreviousPageContents() {

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        val thirdPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page3_title)
        val thirdPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page3_title)

        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(thirdPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(thirdPageDescription))))

        onView(withId(R.id.on_boarding_frame)).perform(swipeRight())

        val secondPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page2_title)
        val secondPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page2_title)

        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(secondPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(secondPageDescription))))

        onView(withId(R.id.on_boarding_frame)).perform(swipeRight())

        val firstPageTitle = intentsTestRule.activity.getString(R.string.on_boarding_page1_title)
        val firstPageDescription = intentsTestRule.activity.getString(R.string.on_boarding_page1_title)
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(firstPageTitle))))
        onView(withId(R.id.on_boarding_frame))
            .check(matches(hasDescendant(withText(firstPageDescription))))


    }

    @Test
    fun goToSignUpActivity_onSignUpClick(){

        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        onView(withId(R.id.sign_up_button))
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
    fun goToLoginActivity_onLoginClick(){


        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())


        onView(withId(R.id.on_boarding_frame)).perform(swipeLeft())

        onView(withId(R.id.login_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Verifies that the SignUpActivity received an intent
        // with the correct package name .

        intended(allOf(
            hasComponent(hasShortClassName(Constants.LOGIN_ACTIVITY_SHORT_CLASS_NAME)),
            toPackage(Constants.PACKAGE_NAME)))
    }


}