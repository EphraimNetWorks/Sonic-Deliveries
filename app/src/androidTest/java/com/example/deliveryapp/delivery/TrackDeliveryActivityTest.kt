package com.example.deliveryapp.delivery

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.MyDate
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.DataBindingIdlingResourceRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class TrackDeliveryActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<TrackDeliveryActivity>
            = ActivityTestRule(TrackDeliveryActivity::class.java)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var testContext: Context

    private lateinit var observerIdlingResource: CountingIdlingResource

    private var testDelivery = Delivery().apply {
        title = "Awesome Package"
        pickUpAddress = "Accra"
        destinationAddress = "Tema"
        deliveryStatus = Delivery.STATUS_IN_TRANSIT
        estimatedTimeOfArrival = 123442134132432

    }

    @Before
    fun setUp(){
        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testDelivery))

        observerIdlingResource = CountingIdlingResource("Cancel Delivery Observer")
        IdlingRegistry.getInstance().register(observerIdlingResource)
        activityRule.activity.idlingResource = observerIdlingResource

        testContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun populateViewsWithDeliveryInfo(){

        onView(withId(R.id.item_name_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testDelivery.title)))

        onView(withId(R.id.item_eta_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testDelivery.estimatedTimeOfArrivalDate!!.getDateFormat1())))

        onView(withId(R.id.item_delivery_id_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testDelivery.id.toUpperCase())))

    }

    @Test
    fun timelineRecyclerViewTest(){


    }
}