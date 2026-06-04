package com.example.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.os.VibrationEffect
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.data.MedicationLog
import com.example.data.Medicine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MedicineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val medicineId = intent.getIntExtra("medicineId", -1)
        val isPreReminder = intent.getBooleanExtra("isPreReminder", false)
        val timeString = intent.getStringExtra("timeString") ?: "08:00"

        Log.d("MedicineAlarm", "Alarm fired! Action: $action, MedicineId: $medicineId, PreReminder: $isPreReminder, Time: $timeString")

        if (medicineId == -1) return

        val database = AppDatabase.getDatabase(context)
        val medicineDao = database.medicineDao()
        val logDao = database.medicationLogDao()

        when (action) {
            ACTION_MEDICINE_TAKE_NOW -> {
                // Background update of medication intake from notification action button
                CoroutineScope(Dispatchers.IO).launch {
                    val medicine = medicineDao.getMedicineById(medicineId)
                    if (medicine != null) {
                        val updatedQty = if (medicine.qtyRemaining > 0) medicine.qtyRemaining - 1 else 0
                        medicineDao.updateMedicine(medicine.copy(qtyRemaining = updatedQty))
                        
                        logDao.insertLog(
                            MedicationLog(
                                medicineId = medicine.id,
                                medicineName = medicine.name,
                                dosage = medicine.dosage,
                                status = "Taken",
                                notes = "Confirmed instantly via high-priority alarm notification."
                            )
                        )
                        Log.d("MedicineAlarm", "Marked ${medicine.name} as Taken via receiver notification button.")
                        
                        // Cancel current alarm notification
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(medicineId)
                    }
                }
            }
            ACTION_MEDICINE_SNOOZE -> {
                val snoozeMinutes = intent.getIntExtra("snoozeMinutes", 5)
                CoroutineScope(Dispatchers.IO).launch {
                    val medicine = medicineDao.getMedicineById(medicineId)
                    if (medicine != null) {
                        AlarmScheduler.scheduleSnoozeAlarm(context, medicine, snoozeMinutes)
                        Log.d("MedicineAlarm", "Snoozed alarm for ${medicine.name} by $snoozeMinutes minutes.")
                        
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(medicineId)
                    }
                }
            }
            ACTION_MEDICINE_SKIP -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val medicine = medicineDao.getMedicineById(medicineId)
                    if (medicine != null) {
                        logDao.insertLog(
                            MedicationLog(
                                medicineId = medicine.id,
                                medicineName = medicine.name,
                                dosage = medicine.dosage,
                                status = "Skipped",
                                notes = "Skipped via interactive alarm notification trigger."
                            )
                        )
                        Log.d("MedicineAlarm", "Marked ${medicine.name} as Skipped via receiver notification button.")
                        
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(medicineId)
                    }
                }
            }
            else -> {
                // This is an actual alert schedule firing (either pre-reminder or exact alarm)
                CoroutineScope(Dispatchers.IO).launch {
                    val medicine = medicineDao.getMedicineById(medicineId)
                    if (medicine != null) {
                        triggerNotification(context, medicine, isPreReminder, timeString)
                        // Trigger device feedback (sound and vibration)
                        playVibrationAndSound(context, isPreReminder)
                    }
                }
            }
        }
    }

    private fun triggerNotification(context: Context, medicine: Medicine, isPreReminder: Boolean, timeString: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medtrack_reminders_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medication Schedule Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Hospital-grade alerts for medical adherence compliance checking"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap notification to open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("activeAlarmMedicineId", medicine.id) // open straight to dashboard overlay
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            medicine.id * 10,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification builders
        val title = if (isPreReminder) "⚠️ Medicine approaching in 1 min" else "⏰ Dose Time: ${medicine.name}"
        val formattedMsg = if (isPreReminder) {
            "Your ${medicine.dosage} dose of ${medicine.name} is scheduled at $timeString. Prepare your medicine."
        } else {
            "Dosage: ${medicine.dosage} (${medicine.instructions}). Please take your medication now."
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(formattedMsg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        if (!isPreReminder) {
            // Interactive quick action buttons
            val takeIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = ACTION_MEDICINE_TAKE_NOW
                putExtra("medicineId", medicine.id)
            }
            val takePending = PendingIntent.getBroadcast(
                context,
                medicine.id * 10 + 1,
                takeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val snoozeIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = ACTION_MEDICINE_SNOOZE
                putExtra("medicineId", medicine.id)
                putExtra("snoozeMinutes", 5)
            }
            val snoozePending = PendingIntent.getBroadcast(
                context,
                medicine.id * 10 + 2,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val skipIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = ACTION_MEDICINE_SKIP
                putExtra("medicineId", medicine.id)
            }
            val skipPending = PendingIntent.getBroadcast(
                context,
                medicine.id * 10 + 3,
                skipIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(android.R.drawable.checkbox_on_background, "Take Now", takePending)
            builder.addAction(android.R.drawable.ic_menu_recent_history, "Snooze 5m", snoozePending)
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Skip", skipPending)
            builder.setFullScreenIntent(contentPendingIntent, true) // Launches high-urgency lock screen
        }

        notificationManager.notify(medicine.id, builder.build())
    }

    private fun playVibrationAndSound(context: Context, isPreReminder: Boolean) {
        try {
            // Trigger Vibration
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (isPreReminder) {
                // Short double vibration
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100), -1))
            } else {
                // Continuous loud alarm vibration (repeat every minute or persistent waveform)
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500, 200), -1))
            }

            // Trigger Alert Sound
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone.play()
            
            // Auto stop ringtone after 5 seconds to avoid annoyance
            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(5000)
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }
        } catch (e: Exception) {
            Log.e("MedicineAlarm", "Failed playing vibration/sound feedback: ${e.message}")
        }
    }

    companion object {
        const val ACTION_MEDICINE_ALERT = "com.example.ACTION_MEDICINE_ALARM"
        const val ACTION_MEDICINE_TAKE_NOW = "com.example.ACTION_MEDICINE_TAKE_NOW"
        const val ACTION_MEDICINE_SNOOZE = "com.example.ACTION_MEDICINE_SNOOZE"
        const val ACTION_MEDICINE_SKIP = "com.example.ACTION_MEDICINE_SKIP"
    }
}
