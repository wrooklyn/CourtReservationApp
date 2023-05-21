package it.polito.mad.courtreservationapp.utils

import android.content.Context
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


object NotificationHelper {
    val db = RemoteDataSource.instance
    fun init(context : Context){
        val sharedPrefs = context?.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs?.getBoolean("isFirstLaunch", true)
        if(isFirstLaunch == null || isFirstLaunch){
            //move code here theoretically
        }
        val userRef = db.collection("users").document(SavedPreference.EMAIL)
        runBlocking {
            launch {
                val id = FirebaseInstallations.getInstance().getToken(false).await()
                println("id: ${id.token}")
                val fields : HashMap<String, Any> = hashMapOf("push_notification_id" to id.token)
                userRef.update(fields)
                sharedPrefs?.getString("notification_id", id.token)
            }
        }


    }

    fun sendPushNotificationToDevice(deviceToken: String, title: String, body: String) {
        val fm = FirebaseMessaging.getInstance()
        fm.send(
            RemoteMessage.Builder("cwu77fgVSOeKIEtQrEe5" + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(1))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build()
        )
        println("message sent maybe")
    }


}