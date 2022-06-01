package com.example.pushnotification

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.pushnotification.services.FirebaseService
import com.example.pushnotification.services.NotificationData
import com.example.pushnotification.services.PushNotificationData
import com.example.pushnotification.services.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

val TAG = "PushNotificationException"
const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        val token = findViewById<EditText>(R.id.token)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("MyToken",it.toString())
        }

        button.setOnClickListener {
            val title = "Some title"
            val text = "Some Text"

            prepareNotification(title,text,token.text.toString())
        }
    }
    private fun prepareNotification(title: String, message: String, token: String) {
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it.toString()
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        PushNotificationData(
            NotificationData(title, message),
            token
        ).also {
            sendNotification(it)
        }
    }

    @SuppressLint("LongLogTag")
    private fun sendNotification(notification: PushNotificationData) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (!response.isSuccessful)
                    Log.e(TAG, response.errorBody().toString())

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
}