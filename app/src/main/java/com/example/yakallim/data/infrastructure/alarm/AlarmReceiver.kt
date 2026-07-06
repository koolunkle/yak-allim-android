package com.example.yakallim.data.infrastructure.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.yakallim.MainActivity
import com.example.yakallim.R
import com.example.yakallim.util.notificationManager

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "MEDICATION_ALARM_CHANNEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(AlarmExtraSpec.KEY_MEDICINE_NAME) ?: ""
        val bigText = intent.getStringExtra(AlarmExtraSpec.KEY_INSTRUCTION) ?: ""

        val notificationManager = context.notificationManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.alarm_channel_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            title.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.alarm_content_title_format, title))
            .setContentText(context.getString(R.string.alarm_content_text))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.alarm_big_text_format, bigText))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(title.hashCode(), notification)
    }
}