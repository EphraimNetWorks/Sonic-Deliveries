package com.example.deliveryapp.ui.main

import android.view.View
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.*
import io.mockk.coEvery
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    @Mock
    lateinit var userRepo: UserRepository

    @Mock
    lateinit var deliveryRepo: DeliveryRepository

    private lateinit var app:AndroidTestApplication

    private val placedDeliveries = listOf(
        Delivery().apply {
            id = "1"
            title = "One"
            createdAt = 1565434531000
            updatedAt = 1565434531000
            deliveryStatus = Delivery.STATUS_PLACED},
        Delivery().apply {
            id = "2"
            title = "Two"
            createdAt = 1565435531000
            updatedAt = 1565435531000
            deliveryStatus = Delivery.STATUS_PLACED})
    private val inTransitDeliveries = listOf(
        Delivery().apply {
            id = "3"
            title = "Three"
            createdAt = 1565435531000
            updatedAt = 1565436531000
            deliveryStatus = Delivery.STATUS_IN_TRANSIT},
        Delivery().apply {
            id = "3.5"
            title = "Three And Half"
            createdAt = 1565435531000
            updatedAt = 1565436531000
            deliveryStatus = Delivery.STATUS_IN_TRANSIT})
    private val completedDeliveries = listOf(
        Delivery().apply {
            id = "4"
            title = "Four"
            createdAt = 1565435531000
            updatedAt = 1565437531000
            deliveryStatus = Delivery.STATUS_COMPLETED},
        Delivery().apply {
            id = "5"
            title = "Five"
            createdAt = 1565435531000
            updatedAt = 1565438531000
            deliveryStatus = Delivery.STATUS_COMPLETED},
        Delivery().apply {
            id = "6"
            title = "Six"
            createdAt = 1565435531000
            updatedAt = 1565439531000
            deliveryStatus = Delivery.STATUS_CANCELLED})

    private val testUser = User().apply {
        id = "252"
        name = "Ephraim Nartey"
    }

    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        TestAppInjector(TestMainModule(userRepo,deliveryRepo)).inject()

        coEvery { userRepo.getCurrentUser() }.returns (testUser)

        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AndroidTestApplication

        Mockito.doAnswer{MockRoomDataSource.mockDataSourceFactory(placedDeliveries)}
            .`when`(deliveryRepo.getDeliveriesPlaced())
        Mockito.doAnswer{MockRoomDataSource.mockDataSourceFactory(inTransitDeliveries)}
            .`when`(deliveryRepo.getDeliveriesInTransit())
        Mockito.doAnswer{MockRoomDataSource.mockDataSourceFactory(completedDeliveries)}
            .`when`(deliveryRepo.getCompletedDeliveries())

    }

    @Test
    fun showNewUserSalutation(){

        activityRule.launchActivity(MainActivity.newInstance(app,MainActivity.SALUTATION_TYPE_SIGN_UP))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        Espresso.onView(ViewMatchers.withId(R.id.salutation_textview))
            .check(ViewAssertions.matches(ViewMatchers.withText(activityRule.activity.getString(R.string.new_user_salutation))))

    }

    @Test
    fun showNewLoginSalutation(){

        activityRule.launchActivity(MainActivity.newInstance(app,MainActivity.SALUTATION_TYPE_NEW_LOGIN))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        Espresso.onView(ViewMatchers.withId(R.id.salutation_textview))
            .check(ViewAssertions.matches(ViewMatchers.withText(activityRule.activity.getString(R.string.new_login_salutation))))

    }

    @Test
    fun showAlreadyLoggedInUserSalutation(){

        activityRule.launchActivity(MainActivity.newInstance(app,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        val salutations = ArrayList<String>().apply {
            addAll(activityRule.activity.resources.getStringArray(R.array.old_user_salutations))
        }

        Espresso.onView(ViewMatchers.withId(R.id.salutation_textview))
            .check(ViewAssertions.matches(
                CustomMatchers.withTextInList(salutations)))

    }

    @Test
    fun showUserDetailsAndDeliverySummary(){

        normalMainActivityLaunch()

        Espresso.onView(ViewMatchers.withId(R.id.profile_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(testUser.name)))

        Espresso.onView(ViewMatchers.withId(R.id.placed_deliveries_no))
            .check(ViewAssertions.matches(ViewMatchers.withText(placedDeliveries.size.toString())))

        Espresso.onView(ViewMatchers.withId(R.id.pending_deliveries_no))
            .check(ViewAssertions.matches(ViewMatchers.withText(inTransitDeliveries.size.toString())))

        Espresso.onView(ViewMatchers.withId(R.id.completed_deliveries_no))
            .check(ViewAssertions.matches(ViewMatchers.withText(completedDeliveries.size.toString())))
    }

    @Test
    fun showMyDeliveries(){

        normalMainActivityLaunch()

        RecyclerViewInteraction<Delivery>(ViewMatchers.withId(R.id.placed_deliveries_recycler_view))
            .withItems(placedDeliveries)
            .customCheck(object :RecyclerViewInteraction.ItemViewAssertion<Delivery>{
                override fun itemViewCheck(item: Delivery, view: View, e: NoMatchingViewException?) {
                    ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(item.title)))
                        .check(view, e)
                    ViewAssertions.matches(ViewMatchers.hasDescendant(
                        ViewMatchers.withText(item.pickUpTimeDate!!.getDateFormat1())))
                        .check(view, e)
                }
            })

        RecyclerViewInteraction<Delivery>(ViewMatchers.withId(R.id.in_transit_deliveries_recycler_view))
            .withItems(inTransitDeliveries)
            .customCheck(object :RecyclerViewInteraction.ItemViewAssertion<Delivery>{
                override fun itemViewCheck(item: Delivery, view: View, e: NoMatchingViewException?) {
                    ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(item.title)))
                        .check(view, e)
                    ViewAssertions.matches(ViewMatchers.hasDescendant(
                        ViewMatchers.withText(item.estimatedTimeOfArrivalDate!!.getDateFormat1())))
                        .check(view, e)
                }
            })

        RecyclerViewInteraction<Delivery>(ViewMatchers.withId(R.id.completed_deliveries_recycler_view))
            .withItems(completedDeliveries)
            .customCheck(object :RecyclerViewInteraction.ItemViewAssertion<Delivery>{
                override fun itemViewCheck(item: Delivery, view: View, e: NoMatchingViewException?) {
                    ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(item.title)))
                        .check(view, e)
                }
            })

    }

    @Test
    fun goToNewDeliveryActivity() {

        normalMainActivityLaunch()

        Espresso.onView(ViewMatchers.withId(R.id.new_delivery_fab))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.NEW_DELIVERY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME)
            )
        )

    }

    @Test
    fun clickOnDeliveryPlacedItemToGoToTrackDeliveryActivity() {

        normalMainActivityLaunch()

        val positionToClick = 0

        Espresso.onView(ViewMatchers.withId(R.id.placed_deliveries_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DeliveryPagingAdapter.DeliveryViewHolder>(positionToClick, ViewActions.click()))

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.TRACK_DELIVERY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME),
                IntentMatchers.hasExtra(Matchers.equalTo(TrackDeliveryActivity.DELIVERY_EXTRA),
                    Matchers.equalTo(placedDeliveries[positionToClick]))
            )
        )

    }

    @Test
    fun clickOnDeliveryInTransitItemToGoToTrackDeliveryActivity() {

        normalMainActivityLaunch()

        val positionToClick = 0

        Espresso.onView(ViewMatchers.withId(R.id.in_transit_deliveries_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DeliveryPagingAdapter.DeliveryViewHolder>(positionToClick, ViewActions.click()))

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.TRACK_DELIVERY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME),
                IntentMatchers.hasExtra(Matchers.equalTo(TrackDeliveryActivity.DELIVERY_EXTRA),
                    Matchers.equalTo(inTransitDeliveries[positionToClick]))
            )
        )

    }

    @Test
    fun clickOnDeliveryCompletedItemToGoToTrackDeliveryActivity() {

        normalMainActivityLaunch()

        val positionToClick = 0

        Espresso.onView(ViewMatchers.withId(R.id.completed_deliveries_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DeliveryPagingAdapter.DeliveryViewHolder>(0, ViewActions.click()))

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.TRACK_DELIVERY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME),
                IntentMatchers.hasExtra(Matchers.equalTo(TrackDeliveryActivity.DELIVERY_EXTRA),
                    Matchers.equalTo(completedDeliveries[positionToClick]))
            )
        )

    }

    private fun normalMainActivityLaunch(){

        activityRule.launchActivity(MainActivity.newInstance(app,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

}