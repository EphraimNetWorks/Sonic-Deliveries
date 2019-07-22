package com.example.deliveryapp.data.remote

import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber


class ApiService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val gson = Gson()

    private var userId :String? = null
        get() {return if(auth.currentUser == null) null else auth.currentUser!!.uid}

    fun loginUser(email:String, password:String, callback:ApiCallback<User?>){
        Timber.d("login details email: $email, password: $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    val fUser = task.result?.user!!
                    db.collection(COLLECTION_USERS).document(fUser.uid).get()
                        .addOnCompleteListener { task1->
                            if(task.isSuccessful){
                                callback.onSuccess(task1.result!!.toObject(User::class.java))
                            }else{
                                callback.onFailed(task1.exception!!.message!!)
                                Timber.w("read user failed with error:${task.exception!!.message!!}")
                            }
                        }
                }else{
                    callback.onFailed(task.exception!!.message!!)
                    Timber.w("login user failed with error:${task.exception!!.message!!}")
                }
            }
    }

    fun signUpUser(signUpRequest: SignUpRequest, callback: ApiCallback<User?>){
        auth.createUserWithEmailAndPassword(signUpRequest.email,signUpRequest.password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val user = task.result?.user
                    val sonicUser = User().apply {
                        id = user!!.uid
                        name = signUpRequest.name
                        phone = signUpRequest.phone
                        email = signUpRequest.email
                    }
                    db.collection(COLLECTION_USERS).document(sonicUser.id).set(sonicUser).addOnCompleteListener { task2 ->
                        if(task2.isSuccessful){
                            callback.onSuccess(sonicUser)
                        }else{
                            callback.onFailed(task2.exception!!.message!!)
                            Timber.w("save signedup user failed with error:${task.exception!!.message!!}")
                        }
                    }
                }else{
                    callback.onFailed(task.exception!!.message!!)
                    Timber.w("sign up user failed with error:${task.exception!!.message!!}")
                }
            }
    }

    fun loadMyDeliveries(callback: ApiCallback<List<Delivery>>){

        db.collection(COLLECTION_DELIVERIES).document(userId!!).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val deliveries = gson.fromJson<List<Delivery>>(gson.toJson(task.result?.data),
                    object :TypeToken<List<Delivery>>(){}.type)
                callback.onSuccess(deliveries?: listOf())
            }else{
                callback.onFailed(task.exception!!.message!!)
            }
        }
    }

    companion object{

        const val COLLECTION_USERS = "users"
        const val COLLECTION_DELIVERIES = "deliveries"
        const val FIELD_USERS_ID = "id"
        const val FIELD_DELIVERY_ID = "id"

    }
}