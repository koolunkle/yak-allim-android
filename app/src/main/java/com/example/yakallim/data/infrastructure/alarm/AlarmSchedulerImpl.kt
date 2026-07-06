package com.example.yakallim.data.infrastructure.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.yakallim.data.datasource.local.preference.AlarmPreference
import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import com.example.yakallim.util.alarmManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmPreference: AlarmPreference
) : AlarmScheduler {

    private val alarmManager = context.alarmManager
        ?: throw IllegalStateException("AlarmManager service is not available")

    override suspend fun schedule(
        medicineName: String,
        dosagePerTake: String,
        dailyFrequency: Int,
        durationDays: Int,
        instruction: String
    ) {
        if (dailyFrequency <= 0 || durationDays <= 0) return

        val intervalMillis = 10 * 1000L
        val totalAlarms = dailyFrequency * durationDays

        for (i in 0 until totalAlarms) {
            val scheduledTimeMillis = System.currentTimeMillis() + (intervalMillis * (i + 1))
            val pendingIntent = createPendingIntent(medicineName, instruction, i)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTimeMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTimeMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTimeMillis, pendingIntent)
            }
        }

        alarmPreference.saveAlarmCount(medicineName, totalAlarms)
        alarmPreference.addActiveAlarmMedicine(medicineName)
    }

    override suspend fun cancel(medicineName: String) {
        val alarmCount = alarmPreference.getAlarmCount(medicineName)
        if (alarmCount <= 0) return

        for (i in 0 until alarmCount) {
            val pendingIntent = PendingIntent.getBroadcast(
                context, generateRequestCode(medicineName, i),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        alarmPreference.removeAlarmCount(medicineName)
        alarmPreference.removeActiveAlarmMedicine(medicineName)
    }

    override suspend fun getActiveAlarm(): Set<String> {
        return alarmPreference.getActiveAlarmMedicines()
    }

    private fun generateRequestCode(medicineName: String, index: Int): Int =
        (medicineName.hashCode() and 0x7FFFFFFF) + index

    private fun createPendingIntent(
        medicineName: String,
        instruction: String,
        index: Int
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmExtraSpec.KEY_MEDICINE_NAME, medicineName)
            putExtra(AlarmExtraSpec.KEY_INSTRUCTION, instruction)
        }
        return PendingIntent.getBroadcast(
            context,
            generateRequestCode(medicineName, index),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
