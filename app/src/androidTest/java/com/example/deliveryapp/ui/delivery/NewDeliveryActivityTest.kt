package com.example.deliveryapp.ui.delivery

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Parcelable
import android.widget.DatePicker
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.deliveryapp.AndroidTestApplication
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.repository.DeliveryRepository
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.di.FakeUserRepository
import com.example.deliveryapp.di.TestAppInjector
import com.example.deliveryapp.di.TestMainModule
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.utils.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class NewDeliveryActivityTest {

    @get:Rule
    val activityRule = IntentsTestRule<NewDeliveryActivity>(NewDeliveryActivity::class.java,true,false)

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    @Rule
    @JvmField
    val espressoTestingIdlingResourceRule = EspressoTestingIdlingResourceRule()

    private lateinit var app:AndroidTestApplication

    @Mock
    lateinit var deliveryRepo: DeliveryRepository



    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AndroidTestApplication

        TestAppInjector(FakeUserRepository(),deliveryRepo).newInject()

        activityRule.launchActivity(NewDeliveryActivity.newInstance(app))

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

    }

    @Test
    fun setUpActionBarAndFormFragment(){

        Mockito.`when`(deliveryRepo.getNetworkState())
            .thenReturn(MutableLiveData(NetworkState.LOADING))

        Timber.d("delivery repo: ${deliveryRepo.getNetworkState().value?.status}")

        EspressoTestUtil.disableProgressBarAnimations(activityRule)

        val newActivityTitle = activityRule.activity.getString(R.string.new_delivery)
        Espresso.onView(ViewMatchers.withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(CustomMatchers.withTitle(newActivityTitle)))

        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun itemNameValidation(){


        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.item_name_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_item_name_error))))

        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.item_name_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_item_name_error))))


    }

    @Test
    fun pickUpPointValidation(){


        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .perform(ViewActions.typeText("Box"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_pickup_address_error))))

        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_editext))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_pickup_address_error))))

    }

    @Test
    fun destinationValidation(){

        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .perform(ViewActions.typeText("Box"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_editext))
            .perform(ViewActions.typeText("Kumasi"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.destination_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_destination_address_error))))

        Espresso.onView(ViewMatchers.withId(R.id.destination_editext))
            .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.destination_text_layout))
            .check(ViewAssertions.matches(CustomMatchers.withError(app.getString(R.string.empty_destination_address_error))))

    }

    @Test
    fun selectDateValidation(){

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 2)

        Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker::class.simpleName)))
                    .perform(
                        PickerActions.setDate(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)))

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.MAIN_ACTIVITY_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.PACKAGE_NAME)
            )
        )
    }


    @Test
    fun setUpSummaryFragment(){
        val testNewDelivery = Delivery().apply {
            title = "Box"
            destinationAddress = "Kumasi"
            pickUpAddress = "Tema"
            pickUpTime = 34324
            additionalInfo = "This is additional information"
        }

        activityRule.activity.setUpSummaryFragment(testNewDelivery)

        //do assertions on summary fragment
        Espresso.onView(ViewMatchers.withId(R.id.summary_item_name_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.title)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_pick_up_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.pickUpAddress)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_destination_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.destinationAddress)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_pickup_date_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.pickUpTimeDate!!.getDateFormat1())))

        Espresso.onView(ViewMatchers.withId(R.id.additional_info_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.additionalInfo)))
    }

    @Test
    fun goToSummaryPageAfterFillingForm(){

        val testNewDelivery = Delivery().apply {
            title = "Box"
            destinationAddress = "Kumasi"
            pickUpAddress = "Tema"
            pickUpTime = 34324
            additionalInfo = "This is additional information"
        }

        //complete new delivery form fragment
        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .perform(ViewActions.scrollTo(),ViewActions.typeText(testNewDelivery.title), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_editext))
            .perform(ViewActions.scrollTo(),ViewActions.typeText(testNewDelivery.pickUpAddress), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.destination_editext))
            .perform(ViewActions.scrollTo(),ViewActions.typeText(testNewDelivery.destinationAddress), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.delivery_date_select_button))
            .perform(ViewActions.scrollTo(),
                ViewActions.typeText(testNewDelivery.pickUpTimeDate!!.getDateFormat1()), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.additional_info_edittext))
            .perform(ViewActions.scrollTo(),ViewActions.typeText(testNewDelivery.additionalInfo), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.next_button))
            .perform(ViewActions.click())

        //do assertions on summary fragment
        Espresso.onView(ViewMatchers.withId(R.id.summary_item_name_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.title)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_pick_up_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.pickUpAddress)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_destination_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.destinationAddress)))

        Espresso.onView(ViewMatchers.withId(R.id.summary_pickup_date_textview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.pickUpTimeDate!!.getDateFormat1())))

        Espresso.onView(ViewMatchers.withId(R.id.summary_addi_info_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(testNewDelivery.additionalInfo)))

    }

    @Test
    fun clickBackButton(){
        val testNewDelivery = Delivery().apply {
            title = "Box"
            destinationAddress = "Kumasi"
            pickUpAddress = "Tema"
            pickUpTime = 34324
            additionalInfo = "This is additional information"
        }

        activityRule.activity.setUpSummaryFragment(testNewDelivery)

        Espresso.onView(ViewMatchers.withId(R.id.back_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.item_name_editext))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun selectPickUpPlaceFromSearch(){

        Espresso.onView(ViewMatchers.withId(R.id.pick_up_search_location))
            .perform(ViewActions.click())

        val testAddress = "HSE NO# Z 102 - Potato Street"
        val testName = "Sakumono"
        val latLng = LatLng((0).toDouble(),(0).toDouble())

        val resultData = Intent()
        val testPlaceResult = Place.builder()
            .setAddress(testAddress)
            .setName("Sakumono")
            .setLatLng(latLng)

        resultData.putExtra("selected_place",testPlaceResult as Parcelable)
        Intents.intending(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.GMAPS_AUTOCOMPLETE_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.GOOGLE_MAPS_PACKAGE_NAME)
            )
        ).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK,resultData))

        val testPlaceAddress = " $testName, $testAddress"
        Espresso.onView(ViewMatchers.withId(R.id.pick_up_point_editext))
            .check(ViewAssertions.matches(ViewMatchers.withText(testPlaceAddress)))
    }

    @Test
    fun selectDestinationUpPlaceFromSearch(){

        Espresso.onView(ViewMatchers.withId(R.id.destination_search_location))
            .perform(ViewActions.click())

        val testAddress = "HSE NO# Z 102 - Potato Street"
        val testName = "Sakumono"
        val latLng = LatLng((0).toDouble(),(0).toDouble())

        val resultData = Intent()
        val testPlaceResult = Place.builder()
            .setAddress(testAddress)
            .setName(testName)
            .setLatLng(latLng)

        resultData.putExtra("selected_place",testPlaceResult.build())
        Intents.intending(
            Matchers.allOf(
                IntentMatchers.hasComponent(ComponentNameMatchers.hasShortClassName(Constants.GMAPS_AUTOCOMPLETE_SHORT_CLASS_NAME)),
                IntentMatchers.toPackage(Constants.GOOGLE_MAPS_PACKAGE_NAME)
            )
        ).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK,resultData))

        val testPlaceAddress = " $testName, $testAddress"
        Espresso.onView(ViewMatchers.withId(R.id.destination_editext))
            .check(ViewAssertions.matches(ViewMatchers.withText(testPlaceAddress)))
    }
}