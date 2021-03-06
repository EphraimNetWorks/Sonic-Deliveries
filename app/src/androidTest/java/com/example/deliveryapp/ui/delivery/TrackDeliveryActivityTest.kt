package com.example.deliveryapp.ui.delivery

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import com.example.deliveryapp.utils.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest


@HiltAndroidTest
class TrackDeliveryActivityTest {


    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule: ActivityTestRule<TrackDeliveryActivity>
            = ActivityTestRule(TrackDeliveryActivity::class.java,false, false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()


    private lateinit var testContext: Context

    private var testNormalDelivery = Delivery().apply {
        id = "DAFDAE"
        title = "Awesome Package"
        pickUpAddress = "Accra"
        destinationAddress = "Tema"
        deliveryStatus = Delivery.STATUS_IN_TRANSIT
        estimatedTimeOfArrival = 123442134132432

    }

    private var testCancelledDelivery = Delivery().apply {
        id = "efgds"
        title = "Not So Awesome Package"
        pickUpAddress = "Kumasi"
        destinationAddress = "Tema"
        deliveryStatus = Delivery.STATUS_CANCELLED
        deliveryTime = 123442134132432

    }

    private val statusStringIds = listOf(
        R.string.delivery_order_placed,
        R.string.package_in_transit,
        R.string.delivery_complete,
        R.string.delivery_cancelled
    )

    @Before
    fun setUp(){

        testContext = InstrumentationRegistry.getInstrumentation().targetContext

    }

    @Test
    fun setUpActionBar(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        val trackActivityTitle = activityRule.activity.getString(R.string.track_delivery)
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
            .check(matches(CustomMatchers.withTitle(trackActivityTitle)))
    }


    @Test
    fun populateViewsWithDeliveryInfo(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        onView(withId(R.id.item_name_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testNormalDelivery.title)))

        onView(withId(R.id.item_eta_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testNormalDelivery.estimatedTimeOfArrivalDate!!.getDateFormat1())))

        onView(withId(R.id.item_delivery_id_textview))
            .check(matches(isDisplayed()))
            .check(matches(withText(testNormalDelivery.id.toUpperCase())))

    }

    @Test
    fun timelineRecyclerViewTest_activeDelivery(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)


        for(position in 0 until 3) {
            //check if it has been completed is shown on last item
            val itemStatus = statusStringIds[position]

            onView(withId(R.id.delivery_timeline_recycler))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(
                    RecyclerItemViewAssertion(
                        position,
                        itemStatus,
                        object : RecyclerViewInteraction.ItemViewAssertion<Int> {
                            override fun itemViewCheck(item: Int, view: View, e: NoMatchingViewException?) {
                                matches(hasDescendant(withText(activityRule.activity.getString(item))))
                                    .check(view, e)
                            }
                        })
                )

        }

    }

    @Test
    fun timelineRecyclerViewTest_cancelledDelivery(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testCancelledDelivery))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        for(position in 0 until 3) {
            //check if it has been cancelled on last item
            val itemStatus = if(position==2) statusStringIds[3] else statusStringIds[position]

            onView(withId(R.id.delivery_timeline_recycler))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(
                    RecyclerItemViewAssertion(
                        position,
                        itemStatus,
                        object : RecyclerViewInteraction.ItemViewAssertion<Int> {
                            override fun itemViewCheck(item: Int, view: View, e: NoMatchingViewException?) {
                                matches(hasDescendant(withText(activityRule.activity.getString(item))))
                                    .check(view, e)
                            }
                        })
                )

        }
    }
}