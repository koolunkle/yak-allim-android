package com.example.yakallim.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, resId, duration).show()
}

val Context.notificationManager: NotificationManager?
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

val Context.alarmManager: AlarmManager?
    get() = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
