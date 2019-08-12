package com.example.deliveryapp.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.deliveryapp.data.local.LocalDatabase
import com.example.deliveryapp.data.local.dao.UserDao
import com.example.deliveryapp.data.local.entities.User
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var userDao: UserDao
    private lateinit var db: LocalDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LocalDatabase::class.java).allowMainThreadQueries().build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadCurrentUser_deleteUserAndVerifyDeletion() {
        val user: User = User().apply {
            id = "1"
            name = "Ephraim"
        }
        userDao.saveUser(user)
        var result = userDao.getCurrentUser()
        assertThat(user, equalTo(result!!.value))

        userDao.deleteUser(user)
        result = userDao.getCurrentUser()
        assertThat(null, equalTo(result!!.value))
    }

}