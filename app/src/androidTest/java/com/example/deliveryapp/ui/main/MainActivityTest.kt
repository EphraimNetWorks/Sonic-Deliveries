package com.example.deliveryapp.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.dao.DeliveryDao
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.ui.track_delivery.TrackDeliveryActivity
import com.example.deliveryapp.utils.*
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivityTest {

    @get:Rule
    val activityRule = IntentsTestRule<MainActivity>(MainActivity::class.java, true, false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var deliveryDao: DeliveryDao

    private lateinit var app:AndroidTestApplication

    private val placedDeliveries = listOf(
        Delivery().apply {
            id = "2"
            title = "Two"
            createdAt = 1565435531000
            updatedAt = 1565435531000
            pickUpTime = 1565437531000
            deliveryStatus = Delivery.STATUS_PLACED},
        Delivery().apply {
            id = "1"
            title = "One"
            createdAt = 1565434531000
            updatedAt = 1565434531000
            pickUpTime = 1565437531000
            deliveryStatus = Delivery.STATUS_PLACED})
    private val inTransitDeliveries = listOf(
        Delivery().apply {
            id = "3.5"
            title = "Three And Half"
            createdAt = 1565436531000
            updatedAt = 1565437531000
            estimatedTimeOfArrival = 1565437531000
            deliveryStatus = Delivery.STATUS_IN_TRANSIT},
        Delivery().apply {
            id = "3"
            title = "Three"
            createdAt = 1565435531000
            updatedAt = 1565436531000
            estimatedTimeOfArrival = 1565436531000
            deliveryStatus = Delivery.STATUS_IN_TRANSIT})
    private val completedDeliveries = listOf(
        Delivery().apply {
            id = "6"
            title = "Six"
            createdAt = 1565435531000
            updatedAt = 1565439531000
            deliveryTime = 1565439531000
            deliveryStatus = Delivery.STATUS_CANCELLED},
        Delivery().apply {
            id = "5"
            title = "Five"
            createdAt = 1565435531000
            updatedAt = 1565438531000
            deliveryTime = 1565438531000
            deliveryStatus = Delivery.STATUS_COMPLETED},
        Delivery().apply {
            id = "4"
            title = "Four"
            createdAt = 1565435531000
            updatedAt = 1565437531000
            deliveryTime = 1565437531000
            deliveryStatus = Delivery.STATUS_COMPLETED})

    private val testUser = User().apply {
        id = "252"
        name = "Ephraim Nartey"
    }

    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        TestAppInjector.inject{it.inject(this)}

        val testUserLD = MutableLiveData(testUser)

        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AndroidTestApplication

        val placedDS= MockRoomDataSource.mockDataSourceFactory(placedDeliveries)
        val inTransitDS= MockRoomDataSource.mockDataSourceFactory(inTransitDeliveries)
        val completedDS= MockRoomDataSource.mockDataSourceFactory(completedDeliveries)

        Mockito.`when`(deliveryDao.getDeliveriesPlaced())
            .thenReturn(placedDS)
        Mockito.`when`(deliveryDao.getDeliveriesInTransit())
            .thenReturn(inTransitDS)
        Mockito.`when`(deliveryDao.getCompletedDeliveries())
            .thenReturn(completedDS)

        Mockito.`when`(userDao.getCurrentUser()).thenReturn (testUserLD)


        Mockito.doAnswer {
            val callback = it.getArgument<ApiCallback<List<Delivery>>>(0)
            callback.onSuccess(completedDeliveries)
        }.`when`(apiService).loadMyDeliveries(com.nhaarman.mockitokotlin2.any())

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

        Espresso.onView(ViewMatchers.withId(R.id.placed_deliveries_recycler_view))
            .perform(ViewActions.scrollTo())

        Thread.sleep(3000)
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

        Espresso.onView(ViewMatchers.withId(R.id.in_transit_deliveries_recycler_view))
            .perform(ViewActions.scrollTo())

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


        Espresso.onView(ViewMatchers.withId(R.id.completed_deliveries_recycler_view))
            .perform(ViewActions.scrollTo())

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

        Espresso.onView(ViewMatchers.withId(R.id.placed_deliveries_no))
            .perform(ViewActions.scrollTo())

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
            .perform(ViewActions.scrollTo())

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
            .perform(ViewActions.scrollTo())

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

    @Test
    fun logoutUser(){
        normalMainActivityLaunch()

        Espresso.onView(ViewMatchers.withId(R.id.logout_button))
            .perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.LOGIN_ACTIVITY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME)
            )
        )
    }

    private fun normalMainActivityLaunch(){

        activityRule.launchActivity(MainActivity.newInstance(app,MainActivity.SALUTATION_TYPE_ALREADY_LOGGED_IN))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

}