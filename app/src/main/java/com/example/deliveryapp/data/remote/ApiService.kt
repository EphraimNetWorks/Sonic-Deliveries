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
                                Timber.w("login and read user success")
                            }else{
                                callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                                Timber.w("read user failed with error:${task.exception?.message}")
                            }
                        }
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.w("login user failed with error:${task.exception?.message}")
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
                            Timber.w("sign up user success")
                        }else{
                            callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                            Timber.w("save signedup user failed with error:${task.exception?.message}")
                        }
                    }
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.w("sign up user failed with error:${task.exception?.message}")
                }
            }
    }

    fun loadMyDeliveries(callback: ApiCallback<List<Delivery>>){

        db.collection(COLLECTION_DELIVERIES).document(userId!!).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val deliveries = gson.fromJson<List<Delivery>>(gson.toJson(task.result?.data),
                    object :TypeToken<List<Delivery>>(){}.type)
                callback.onSuccess(deliveries?: listOf())
                Timber.w("load my deliveries success with size: ${deliveries.size}")
            }else{
                callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                Timber.w("load my delivery failed with error:${task.exception?.message}")
            }
        }
    }

    fun cancelDelivery(deliveryId: String, callback: ApiCallback<Boolean>) {

        db.collection(COLLECTION_DELIVERIES).document("${userId!!}/$deliveryId")
            .update("deliveryStatus",Delivery.STATUS_CANCELLED)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    callback.onSuccess(true)
                    Timber.w("cancel delivery success")
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.w("cancel delivery failed with error:${task.exception?.message}")
                }
            }
    }

    fun sendNewDelivery(newDelivery: Delivery, callback: ApiCallback<Boolean>) {
        val deliveryId = db.collection("$COLLECTION_DELIVERIES/$userId").document().id
        newDelivery.id = deliveryId

        db.collection("$COLLECTION_DELIVERIES/$userId").document(deliveryId).set(newDelivery).addOnCompleteListener {task->
            if(task.isSuccessful){
                callback.onSuccess(true)
                Timber.w("send new delivery success")
            }else{
                callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                Timber.w("send new delivery failed with error:${task.exception?.message}")
            }
        }
    }

    companion object{

        const val COLLECTION_USERS = "users"
        const val COLLECTION_DELIVERIES = "deliveries"

        const val UNKNOWN_FAILURE_ERROR = "Unknown reason for failure"

    }
}