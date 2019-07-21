package com.example.deliveryapp.data.local.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.ApiCallback
import com.example.deliveryapp.data.remote.ApiService
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.nhaarman.mockito_kotlin.capture
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.verify




class UserRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService
    @Mock
    lateinit var userDao: UserDao

    @Captor
    lateinit var callbackCaptor:ArgumentCaptor<ApiCallback<User?>>

    lateinit var userRepository: UserRepository

    private val user = User().apply {
        name = "Ephraim Nartey"
        email = "narteyephraim@gmail.com"
        phone = "+233501384237"
        profilePicUrl = ""
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userRepository = UserRepository(apiService,userDao)
    }

    @Test
    fun `set network state to loading on login`(){

        userRepository.login("ffs","sfg")

        assertEquals(
            userRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `process login result when positive callback called`(){

        userRepository.login("ffs","sfg")

        verify(apiService).loginUser(anyString(), anyString(), capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onSuccess(user)

        assertEquals(
            userRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )

    }

    @Test
    fun `process login result when negative callback called`(){
        val errorMessage = "this is an error"

        userRepository.login("ffs","sfg")

        verify(apiService).loginUser(anyString(), anyString(), capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            userRepository.getNetworkState().value!!.message,
            errorMessage
        )

    }


    @Test
    fun `set network state to loading on sign up`(){

        userRepository.login("ffs","sfg")

        assertEquals(
            userRepository.getNetworkState().value!!.status,
            Status.RUNNING
        )

    }

    @Test
    fun `process signup result when positive callback called`(){

        val req = Mockito.mock(SignUpRequest::class.java)
        userRepository.signUp(req)

        verify(apiService).signUpUser(any(SignUpRequest::class.java), capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onSuccess(user)

        assertEquals(
            userRepository.getNetworkState().value!!.status,
            Status.SUCCESS
        )

    }

    @Test
    fun `process signup result when negative callback called`(){
        val req = Mockito.mock(SignUpRequest::class.java)
        val errorMessage = "this is an error"

        userRepository.signUp(req)

        verify(apiService).signUpUser(any(SignUpRequest::class.java), capture(callbackCaptor))

        val callback = callbackCaptor.value

        callback.onFailed(errorMessage)

        assertEquals(
            userRepository.getNetworkState().value!!.message,
            errorMessage
        )

    }

    private fun <T> any(type : Class<T>): T {
        Mockito.any(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}