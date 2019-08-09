package com.example.deliveryapp.delivery

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.EspressoTestUtil
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.models.MyDate
import com.example.deliveryapp.ui.signup.SignUpActivity
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.DataBindingIdlingResourceRule
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.NoMatchingViewException
import com.example.deliveryapp.RecyclerItemViewAssertion
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import com.example.deliveryapp.RecyclerViewInteraction
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.di.FakeUserRepository
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import com.example.deliveryapp.ui.track_delivery.DeliveryTimelineAdapter
import com.example.deliveryapp.utils.CustomMatchers
import junit.framework.Assert.assertTrue
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
@LargeTest
class TrackDeliveryActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<TrackDeliveryActivity>
            = ActivityTestRule(TrackDeliveryActivity::class.java,false, false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var testContext: Context

    private lateinit var observerIdlingResource: CountingIdlingResource

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
        estimatedTimeOfArrival = 123442134132432

    }

    private val statusStringIds = listOf(
        R.string.delivery_order_placed,
        R.string.package_in_transit,
        R.string.delivery_complete,
        R.string.delivery_cancelled
    )
    @Mock
    private lateinit var deliveryRepo: DeliveryRepository

    @Before
    fun setUp(){

        Timber.e("init test")

        MockitoAnnotations.initMocks(this)

        TestAppInjector(TestMainModule(FakeUserRepository(),deliveryRepo)).inject()

        testContext = InstrumentationRegistry.getInstrumentation().targetContext

        testNormalDelivery.title

        observerIdlingResource = CountingIdlingResource("Cancel Delivery Observer")
        IdlingRegistry.getInstance().register(observerIdlingResource)


    }

    @Test
    fun setUpActionBar(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        activityRule.activity.idlingResource = observerIdlingResource

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        val trackActivityTitle = activityRule.activity.getString(R.string.track_delivery)
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
            .check(matches(CustomMatchers.withTitle(trackActivityTitle)))
    }

    @Test
    fun backButtonClosesActivity(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        activityRule.activity.idlingResource = observerIdlingResource

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        Espresso.pressBackUnconditionally()

        assertTrue(activityRule.activity.isDestroyed)
    }

    @Test
    fun populateViewsWithDeliveryInfo(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testNormalDelivery))

        activityRule.activity.idlingResource = observerIdlingResource

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

        activityRule.activity.idlingResource = observerIdlingResource

        EspressoTestUtil.disableProgressBarAnimations(activityRule)


        for(position in 0 until 3) {
            //check if it has been cancelled on last item
            val itemStatus = statusStringIds[position]

            onView(withId(R.id.delivery_timeline_recycler))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(RecyclerItemViewAssertion(position,itemStatus,object : RecyclerViewInteraction.ItemViewAssertion<Int>{
                    override fun itemViewCheck(item: Int, view: View, e: NoMatchingViewException?) {
                        matches(hasDescendant(withText(activityRule.activity.getString(item))))
                            .check(view,e)
                    }
                }))

        }

    }

    @Test
    fun timelineRecyclerViewTest_cancelledDelivery(){

        activityRule.launchActivity(TrackDeliveryActivity.newInstance(testContext,testCancelledDelivery))

        activityRule.activity.idlingResource = observerIdlingResource

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        for(position in 0 until 3) {
            //check if it has been cancelled on last item
            val itemStatus = if(position==2) statusStringIds[3] else statusStringIds[position]

            onView(withId(R.id.delivery_timeline_recycler))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(RecyclerItemViewAssertion(position,itemStatus,object : RecyclerViewInteraction.ItemViewAssertion<Int>{
                    override fun itemViewCheck(item: Int, view: View, e: NoMatchingViewException?) {
                        matches(hasDescendant(withText(activityRule.activity.getString(item))))
                        .check(view,e)
                    }
                }))

        }
    }
}