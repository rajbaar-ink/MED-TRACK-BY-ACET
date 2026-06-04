package com.example.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.Medicine
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAlarmsForMedicine(context: Context, medicine: Medicine) {
        if (!medicine.isNotificationEnabled) {
            cancelAlarmsForMedicine(context, medicine)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Split times (e.g., "08:00,20:00")
        val times = medicine.times
        for ((index, timeStr) in times.withIndex()) {
            val hourAndMinute = timeStr.split(":")
            if (hourAndMinute.size != 2) continue
            
            val hour = hourAndMinute[0].toIntOrNull() ?: continue
            val minute = hourAndMinute[1].toIntOrNull() ?: continue

            // 1. Exact Alarm time calculation
            val calendarExact = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendarExact.timeInMillis <= System.currentTimeMillis()) {
                calendarExact.add(Calendar.DAY_OF_YEAR, 1)
            }

            // 2. Pre-Reminder alarm time calculation (1 minute before exact time)
            val calendarPre = (calendarExact.clone() as Calendar).apply {
                add(Calendar.MINUTE, -1)
            }

            // Standard intent for exact alarm
            val intentExact = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = MedicineAlarmReceiver.ACTION_MEDICINE_ALERT
                putExtra("medicineId", medicine.id)
                putExtra("isPreReminder", false)
                putExtra("timeString", timeStr)
            }
            val pendingExact = PendingIntent.getBroadcast(
                context,
                medicine.id * 100 + index,
                intentExact,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Intent for pre-reminder (1 min early)
            val intentPre = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = MedicineAlarmReceiver.ACTION_MEDICINE_ALERT
                putExtra("medicineId", medicine.id)
                putExtra("isPreReminder", true)
                putExtra("timeString", timeStr)
            }
            val pendingPre = PendingIntent.getBroadcast(
                context,
                medicine.id * 100 + index + 50, // separate request code offset
                intentPre,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Register exact alarms with system
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarExact.timeInMillis,
                            pendingExact
                        )
                        // Pre-schedule
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarPre.timeInMillis,
                            pendingPre
                        )
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarExact.timeInMillis, pendingExact)
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarPre.timeInMillis, pendingPre)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendarExact.timeInMillis,
                        pendingExact
                    )
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendarPre.timeInMillis,
                        pendingPre
                    )
                }
                Log.d("AlarmScheduler", "Scheduled exact alarm and pre-alarm for ${medicine.name} at $timeStr (Millis pre: ${calendarPre.timeInMillis}, Mills exact: ${calendarExact.timeInMillis})")
            } catch (e: Exception) {
                Log.e("AlarmScheduler", "Exact alarm scheduling failed: ${e.message}")
            }
        }
    }

    fun scheduleSnoozeAlarm(context: Context, medicine: Medicine, minutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val snoozeTimeMillis = System.currentTimeMillis() + (minutes * 60 * 1000L)
        val intentSnooze = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = MedicineAlarmReceiver.ACTION_MEDICINE_ALERT
            putExtra("medicineId", medicine.id)
            putExtra("isPreReminder", false)
            putExtra("timeString", "Snoozed $minutes mins")
        }
        val pendingSnooze = PendingIntent.getBroadcast(
            context,
            medicine.id * 1000 + 9991, // unique high request code for snooze
            intentSnooze,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTimeMillis,
                    pendingSnooze
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTimeMillis, pendingSnooze)
            }
            Log.d("AlarmScheduler", "Scheduled snooze alarm for ${medicine.name} in $minutes minutes.")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed setting snooze alarm: ${e.message}")
        }
    }

    fun cancelAlarmsForMedicine(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val times = medicine.times
        for ((index, timeStr) in times.withIndex()) {
            val intentExact = Intent(context, MedicineAlarmReceiver::class.java).apply {
                action = MedicineAlarmReceiver.ACTION_MEDICINE_ALERT
            }
            val pendingExact = PendingIntent.getBroadcast(
                context,
                medicine.id * 100 + index,
                intentExact,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingExact != null) {
                alarmManager.cancel(pendingExact)
                pendingExact.cancel()
            }

            val pendingPre = PendingIntent.getBroadcast(
                context,
                medicine.id * 100 + index + 50,
                intentExact,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingPre != null) {
                alarmManager.cancel(pendingPre)
                pendingPre.cancel()
            }
        }
        Log.d("AlarmScheduler", "Cancelled all alarms for ${medicine.name}")
    }
}
