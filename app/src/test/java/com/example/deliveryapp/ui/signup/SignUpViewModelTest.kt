package com.example.deliveryapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.NetworkState
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.example.deliveryapp.ui.new_delivery.DeliveryFormViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.regex.Pattern

class SignUpViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var userDao: UserDao
    @Mock
    lateinit var localDatabase: LocalDatabase

    lateinit var userRepository: UserRepository

    private lateinit var signUpViewModel:SignUpViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userRepository = UserRepository(apiService,userDao,localDatabase)
        signUpViewModel = SignUpViewModel(userRepository)

        signUpViewModel.PHONE_PATTERN = PHONE_PATTERN
        signUpViewModel.EMAIL_ADDRESS_PATTERN = EMAIL_ADDRESS_PATTERN
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `set initial validation map on init view model`() {
        assertNotNull(signUpViewModel.validationMap)
        signUpViewModel.validationMap.values.forEach { assertEquals(DeliveryFormViewModel.VAL_DEFAULT,it) }
    }

    @Test
    fun `set validation results for all fields after validate login details`() {
        signUpViewModel.validateSignUpDetails("","","","","")
        var validationResultsSet = true
        for(result in signUpViewModel.validationMap.values){
            validationResultsSet = result != SignUpViewModel.VAL_DEFAULT
        }
        assertTrue(validationResultsSet)
    }

    @Test
    fun `set email validation message`() {
        signUpViewModel.validateEmail("")
        assertTrue(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!! != SignUpViewModel.VAL_DEFAULT)
    }

    @Test
    fun `set password validation message`() {
        signUpViewModel.validatePassword("","")
        assertTrue(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!! != SignUpViewModel.VAL_DEFAULT)
    }

    @Test
    fun `set empty email if email is empty`() {
        signUpViewModel.validateEmail("")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.EMPTY_EMAIL_ADDRESS)
    }

    @Test
    fun `set invalid email if email does not match regex`() {
        signUpViewModel.validateEmail("akljfalkjdad")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.INVALID_EMAIL_ADDRESS)
    }

    @Test
    fun `set empty password if password is empty`() {
        signUpViewModel.validatePassword("","")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.EMPTY_PASSWORD)
    }

    @Test
    fun `set passwords don't match if passwords are different`() {
        signUpViewModel.validatePassword("fjkallad","kajkdlkals")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.PASSWORDS_DONT_MATCH)
    }

    @Test
    fun `set invalid password if password does not meet minimum length`() {
        signUpViewModel.validatePassword("akljf","daadadfd")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.INVALID_PASSWORD)
    }

    @Test
    fun `set empty phone if phone is empty`() {
        signUpViewModel.validatePhone("")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.EMPTY_PHONE_NUMBER)
    }

    @Test
    fun `set invalid phone if phone does not match regex`() {
        signUpViewModel.validatePhone("1")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.INVALID_PHONE_NUMBER)
    }


    @Test
    fun `set empty name if name is empty`() {
        signUpViewModel.validateName("")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_NAME_KEY]!!,SignUpViewModel.EMPTY_NAME)
    }

    @Test
    fun `set valid name if name is valid`() {
        signUpViewModel.validateName("Ephraim Nartey")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_NAME_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid phone if phone is valid`() {
        signUpViewModel.validatePhone("+233501384237")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid email if email is valid`() {
        val testEmail = "narteyephraim@gamil.com"

        signUpViewModel.validateEmail(testEmail)
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid password if password is valid`() {
        signUpViewModel.validatePassword("akljfkl","akljfkl")
        assertEquals(signUpViewModel.validationMap[SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set network state on get network state`(){
        signUpViewModel.getNetworkState()
        assertNotNull(signUpViewModel.getNetworkState())
    }

    @Test
    fun `sign up user`(){
        val name = "name"
        val phone = "0240000000"
        val email = "email@email.com"
        val password = "password"
        signUpViewModel.signUpUser(name,phone,email, password)

        verify(apiService,times(1)).signUpUser(ArgumentMatchers.any(SignUpRequest::class.java),any())

    }

    companion object{
        private const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        private val EMAIL_ADDRESS_PATTERN = Pattern.compile(EMAIL_PATTERN)

        private const val PHONE_PATTERN_STRING =
            "^\\+[1-9]{1}[0-9]{3,14}\$"
        private val PHONE_PATTERN = Pattern.compile(PHONE_PATTERN_STRING)
    }
}