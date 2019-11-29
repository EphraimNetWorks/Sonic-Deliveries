package com.example.deliveryapp.data.remote

import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.data.local.entities.User
import com.example.deliveryapp.data.local.models.Location
import com.example.deliveryapp.data.remote.ApiService.Companion.COLLECTION_DELIVERIES
import com.example.deliveryapp.data.remote.ApiService.Companion.COLLECTION_USERS
import com.example.deliveryapp.data.remote.ApiService.Companion.DELIVERY_STATUS_FIELD_NAME
import com.example.deliveryapp.data.remote.ApiService.Companion.DELIVERY_TIME_DATE_FIELD_NAME
import com.example.deliveryapp.data.remote.ApiService.Companion.DELIVERY_TIME_FIELD_NAME
import com.example.deliveryapp.data.remote.ApiService.Companion.DOCUMENT_COLLECTION_MY_DELIVERIES
import com.example.deliveryapp.data.remote.ApiService.Companion.UNKNOWN_FAILURE_ERROR
import com.example.deliveryapp.data.remote.ApiService.Companion.UPDATED_AT_FIELD_NAME
import com.example.deliveryapp.data.remote.request.SignUpRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import org.joda.time.DateTime
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface ApiService {

    fun loginUser(email:String, password:String, callback:ApiCallback<User?>)

    fun signUpUser(signUpRequest: SignUpRequest, callback: ApiCallback<User?>)

    fun loadMyDeliveries(callback: ApiCallback<List<Delivery>>)

    fun cancelDelivery(deliveryId: String, callback: ApiCallback<Boolean>)

    fun sendNewDelivery(newDelivery: Delivery, callback: ApiCallback<Boolean>)

    fun getDirections(origin: Location, destination: Location, apiKey: String,
                      apiCallback: ApiCallback<DirectionsResult>)

    fun logoutUser()

    companion object{

        const val COLLECTION_USERS = "users"
        const val COLLECTION_DELIVERIES = "deliveries"

        const val UNKNOWN_FAILURE_ERROR = "Unknown reason for failure"
        const val DOCUMENT_COLLECTION_MY_DELIVERIES ="my_deliveries"

        const val DELIVERY_STATUS_FIELD_NAME = "deliveryStatus"
        const val DELIVERY_TIME_FIELD_NAME = "deliveryTime"
        const val DELIVERY_TIME_DATE_FIELD_NAME = "deliveryTimeDate"
        const val UPDATED_AT_FIELD_NAME = "updatedAt"

    }
}

class ApiServiceImpl :ApiService{

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId :String?
        get() {return if(auth.currentUser == null) null else auth.currentUser!!.uid}

    override fun loginUser(email:String, password:String, callback:ApiCallback<User?>){
        Timber.d("login details email: $email, password: $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    val fUser = task.result?.user!!
                    db.collection(COLLECTION_USERS).document(fUser.uid).get()
                        .addOnCompleteListener { task1->
                            if(task.isSuccessful){
                                callback.onSuccess(task1.result!!.toObject(User::class.java))
                                Timber.d("login and read user success")
                            }else{
                                callback.onFailed(task1.exception?.message?: UNKNOWN_FAILURE_ERROR)
                                Timber.w("read user failed with error:${task.exception?.message}")
                            }
                        }
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.w("login user failed with error:${task.exception?.message}")
                }
            }
    }

    override fun signUpUser(signUpRequest: SignUpRequest, callback: ApiCallback<User?>){
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
                            Timber.d("sign up user success")
                        }else{
                            callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                            Timber.e("save signedup user failed with error:${task.exception?.message}")
                        }
                    }
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.e("sign up user failed with error:${task.exception?.message}")
                }
            }
    }

    override fun loadMyDeliveries(callback: ApiCallback<List<Delivery>>){

        db.collection(COLLECTION_DELIVERIES).document(userId!!).collection(DOCUMENT_COLLECTION_MY_DELIVERIES).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val deliveries = task.result?.documents?.map { it.toObject(Delivery::class.java)!! }
                callback.onSuccess(deliveries?: listOf())
                Timber.d("load my deliveries success with size: ${deliveries?.size}")
            }else{
                callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                Timber.e("load my delivery failed with error:${task.exception?.message}")
            }
        }
    }

    override fun cancelDelivery(deliveryId: String, callback: ApiCallback<Boolean>) {


        val deliveryRef = db.collection(COLLECTION_DELIVERIES).document(userId!!)
            .collection(DOCUMENT_COLLECTION_MY_DELIVERIES).document(deliveryId)

        val updateMap = mapOf(
            Pair(DELIVERY_STATUS_FIELD_NAME,Delivery.STATUS_CANCELLED),
            Pair(DELIVERY_TIME_FIELD_NAME, DateTime.now().millis),
            Pair(UPDATED_AT_FIELD_NAME, DateTime.now().millis),
            Pair(DELIVERY_TIME_DATE_FIELD_NAME, mapOf(Pair("timeStamp",DateTime.now().millis)))
        )
        deliveryRef
            .update(updateMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    callback.onSuccess(true)
                    Timber.d("cancel delivery success")
                }else{
                    callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                    Timber.e("cancel delivery failed with error:${task.exception?.message}")
                }
            }
    }

    override fun sendNewDelivery(newDelivery: Delivery, callback: ApiCallback<Boolean>) {
        val deliveryId = db.collection(COLLECTION_DELIVERIES).document(userId!!)
            .collection(DOCUMENT_COLLECTION_MY_DELIVERIES).document().id
        newDelivery.id = deliveryId
        newDelivery.createdAt = DateTime.now().millis
        newDelivery.updatedAt = DateTime.now().millis

        val newDeliveryRef = db.collection(COLLECTION_DELIVERIES).document(userId!!)
            .collection(DOCUMENT_COLLECTION_MY_DELIVERIES).document(newDelivery.id)

        newDeliveryRef.set(newDelivery).addOnCompleteListener {task->
            if(task.isSuccessful){
                callback.onSuccess(true)
                Timber.d("send new delivery success")
            }else{
                callback.onFailed(task.exception?.message?: UNKNOWN_FAILURE_ERROR)
                Timber.e("send new delivery failed with error:${task.exception?.message}")
            }
        }
    }

    override fun getDirections(origin: Location, destination: Location, apiKey: String,
                      apiCallback: ApiCallback<DirectionsResult>) {

        val geoApiContext = GeoApiContext.Builder()
            .queryRateLimit(3)
            .apiKey(apiKey)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING).origin(LatLng(origin.latitude,origin.longitude))
            .destination(LatLng(destination.latitude,destination.longitude)).departureTime(DateTime())
            .setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    apiCallback.onSuccess(result)
                    Timber.d("Get Directions Result success")
                }

                override fun onFailure(e: Throwable) {
                    apiCallback.onFailed(e.localizedMessage?:"Unable to get directions")
                    Timber.e("Get Directions Result failed with error: %s", e.message)
                }
            })
    }

    override fun logoutUser(){
        auth.signOut()
    }

}