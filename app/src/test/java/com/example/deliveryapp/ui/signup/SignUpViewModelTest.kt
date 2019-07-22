package com.example.deliveryapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.deliveryapp.data.local.repository.UserRepository
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SignUpViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var userRepository: UserRepository

    private lateinit var signUpViewModel:SignUpViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        signUpViewModel = SignUpViewModel(userRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `set initial validation map on init view model`() {
        assertNotNull(signUpViewModel.validationMap.value)
    }

    @Test
    fun `set validation results for all fields after validate login details`() {
        signUpViewModel.validateSignUpDetails("","","","","")
        var validationResultsSet = true
        for(result in signUpViewModel.validationMap.value!!.values){
            validationResultsSet = result.isNotEmpty()
        }
        assertTrue(validationResultsSet)
    }

    @Test
    fun `set email validation message`() {
        signUpViewModel.validateEmail("")
        assertTrue(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_EMAIL_KEY]!!.isNotEmpty())
    }

    @Test
    fun `set password validation message`() {
        signUpViewModel.validatePassword("","")
        assertTrue(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!.isNotEmpty())
    }

    @Test
    fun `set empty email if email is empty`() {
        signUpViewModel.validateEmail("")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.EMPTY_EMAIL_ADDRESS)
    }

    @Test
    fun `set invalid email if email does not match regex`() {
        signUpViewModel.validateEmail("akljfalkjdad")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.INVALID_EMAIL_ADDRESS)
    }

    @Test
    fun `set empty password if password is empty`() {
        signUpViewModel.validatePassword("","")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.EMPTY_PASSWORD)
    }

    @Test
    fun `set passwords don't match if passwords are different`() {
        signUpViewModel.validatePassword("fjkallad","kajkdlkals")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.PASSWORDS_DONT_MATCH)
    }

    @Test
    fun `set invalid password if password does not meet minimum length`() {
        signUpViewModel.validatePassword("akljf","daadadfd")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.INVALID_PASSWORD)
    }

    @Test
    fun `set empty phone if phone is empty`() {
        signUpViewModel.validatePhone("")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.EMPTY_PHONE_NUMBER)
    }

    @Test
    fun `set invalid phone if phone does not match regex`() {
        signUpViewModel.validatePhone("1")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.INVALID_PHONE_NUMBER)
    }


    @Test
    fun `set empty name if name is empty`() {
        signUpViewModel.validateName("")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_NAME_KEY]!!,SignUpViewModel.EMPTY_NAME)
    }

    @Test
    fun `set valid name if name is valid`() {
        signUpViewModel.validateName("Ephraim Nartey")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_NAME_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid phone if phone is valid`() {
        signUpViewModel.validatePhone("+233501384237")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PHONE_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid email if email is valid`() {
        val testEmail = "narteyephraim@gamil.com"

        signUpViewModel.validateEmail(testEmail)
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_EMAIL_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set valid password if password is valid`() {
        signUpViewModel.validatePassword("akljfkl","akljfkl")
        assertEquals(signUpViewModel.validationMap.value!![SignUpViewModel.VAL_MAP_PASSWORD_KEY]!!,SignUpViewModel.VAL_VALID)
    }

    @Test
    fun `set network state on get network state`(){
        assertNotNull(signUpViewModel.getNetworkState().value)
    }
}