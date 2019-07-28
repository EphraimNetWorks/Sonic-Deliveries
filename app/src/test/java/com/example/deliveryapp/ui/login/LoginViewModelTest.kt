package com.example.deliveryapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.repository.UserRepository
import com.example.deliveryapp.utils.DispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.regex.Pattern

class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var userRepository: UserRepository

    private lateinit var loginViewModel:LoginViewModel

    private val user = User().apply {
        name = "Ephraim Nartey"
        email = "narteyephraim@gmail.com"
        phone = "+233501384237"
        profilePicUrl = ""
    }

    private val testProvider = DispatcherProvider(
        IO = Dispatchers.Unconfined,
        Main = Dispatchers.Unconfined
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { userRepository.getCurrentUser() }.returns (user)
        runBlocking {
            loginViewModel = LoginViewModel(userRepository,testProvider)
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `set initial validation map on init view model`() {
        assertNotNull(loginViewModel.validationMap.value)
    }

    @Test
    fun `validate current user`(){
        assertNotNull(loginViewModel.currentUser)
        assertEquals("Ephraim Nartey", loginViewModel.currentUser!!.name)
    }

    @Test
    fun `set validation results for all fields after validate login details`() {
        loginViewModel.validateLoginDetails("","")
        var validationResultsSet = true
        for(result in loginViewModel.validationMap.value!!.values){
            validationResultsSet = result.isNotEmpty()
        }
        assertTrue(validationResultsSet)
    }

    @Test
    fun `set email validation message`() {
        loginViewModel.validateEmail("")
        assertTrue(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_EMAIL_KEY]!!.isNotEmpty())
    }

    @Test
    fun `set password validation message`() {
        loginViewModel.validatePassword("")
        assertTrue(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_PASSWORD_KEY]!!.isNotEmpty())
    }

    @Test
    fun `set empty email if email is empty`() {
        loginViewModel.validateEmail("")
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_EMAIL_KEY]!!,LoginViewModel.EMPTY_EMAIL_ADDRESS)
    }

    @Test
    fun `set invalid email if email does not match regex`() {
        loginViewModel.validateEmail("akljfalkjdad")
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_EMAIL_KEY]!!,LoginViewModel.INVALID_EMAIL_ADDRESS)
    }

    @Test
    fun `set empty password if password is empty`() {
        loginViewModel.validatePassword("")
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_PASSWORD_KEY]!!,LoginViewModel.EMPTY_PASSWORD)
    }

    @Test
    fun `set invalid password if password does not meet minimum length`() {
        loginViewModel.validatePassword("akljf")
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_PASSWORD_KEY]!!,LoginViewModel.INVALID_PASSWORD)
    }

    @Test
    fun `set valid email if email is valid`() {
        val testEmail = "narteyephraim@gamil.com"

        loginViewModel.validateEmail(testEmail)
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_EMAIL_KEY]!!,LoginViewModel.VAL_VALID)
    }

    @Test
    fun `set valid password if password is valid`() {
        loginViewModel.validatePassword("akljfkl")
        assertEquals(loginViewModel.validationMap.value!![LoginViewModel.VAL_MAP_PASSWORD_KEY]!!,LoginViewModel.VAL_VALID)
    }


    companion object{
        private const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        private val EMAIL_ADDRESS_PATTERN = Pattern.compile(EMAIL_PATTERN)
    }

}