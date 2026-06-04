package com.example.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "Generated new secure FCM registration token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMService", "FCM push notification received from acet-medtrack backend: ${remoteMessage.data}")

        // Retrieve medicine specifications from push payload
        val medicineIdStr = remoteMessage.data["medicineId"]
        val medicineId = medicineIdStr?.toIntOrNull() ?: return
        val isPreStr = remoteMessage.data["isPreReminder"] ?: "false"
        val isPre = isPreStr.toBoolean()
        val scheduledTime = remoteMessage.data["timeString"] ?: "08:00"

        // Trigger local alarm immediately from this remote signal
        val context = applicationContext
        val intent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = MedicineAlarmReceiver.ACTION_MEDICINE_ALERT
            putExtra("medicineId", medicineId)
            putExtra("isPreReminder", isPre)
            putExtra("timeString", scheduledTime)
        }
        context.sendBroadcast(intent)
    }
}
