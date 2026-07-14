package com.example.yakallim.data.infrastructure.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.yakallim.data.datasource.local.preference.AlarmPreference
import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import com.example.yakallim.domain.model.Alarm
import com.example.yakallim.util.alarmManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
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
        alarmTimes: List<String>,
        soundUri: String?
    ) {
        if (dailyFrequency <= 0 || durationDays <= 0 || alarmTimes.isEmpty()) return

        val totalAlarms = dailyFrequency * durationDays
        var alarmIndex = 0

        for (day in 0 until durationDays) {
            for (timeStr in alarmTimes) {
                if (alarmIndex >= totalAlarms) break

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    add(Calendar.DAY_OF_YEAR, day)

                    val parts = timeStr.split(":")
                    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val scheduledTimeMillis = calendar.timeInMillis
                if (scheduledTimeMillis > System.currentTimeMillis()) {
                    val pendingIntent = createPendingIntent(
                        medicineName,
                        dosagePerTake,
                        dailyFrequency,
                        durationDays,
                        alarmIndex,
                        soundUri
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                scheduledTimeMillis,
                                pendingIntent
                            )
                        } else {
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                scheduledTimeMillis,
                                pendingIntent
                            )
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            scheduledTimeMillis,
                            pendingIntent
                        )
                    }
                }
                alarmIndex++
            }
        }

        alarmPreference.saveAlarmCount(medicineName, totalAlarms)
        alarmPreference.addActiveAlarmMedicine(medicineName)
        alarmPreference.saveDetailAlarm(medicineName, alarmTimes, soundUri)
    }

    override suspend fun cancel(medicineName: String) {
        val alarmCount = alarmPreference.getAlarmCount(medicineName)
        if (alarmCount <= 0) return

        for (i in 0 until alarmCount) {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.setPackage(context.packageName)
            val pendingIntent = PendingIntent.getBroadcast(
                context, generateRequestCode(medicineName, i),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        alarmPreference.removeAlarmCount(medicineName)
        alarmPreference.removeActiveAlarmMedicine(medicineName)
        alarmPreference.removeDetailAlarm(medicineName)
    }

    override suspend fun getActiveAlarm(): Set<String> {
        return alarmPreference.getActiveAlarmMedicines()
    }

    override suspend fun getDetailAlarm(medicineName: String): Alarm? {
        return alarmPreference.getDetailAlarm(medicineName)
    }

    private fun generateRequestCode(medicineName: String, index: Int): Int =
        (medicineName.hashCode() and 0x7FFFFFFF) + index

    private fun createPendingIntent(
        medicineName: String,
        dosagePerTake: String,
        dailyFrequency: Int,
        durationDays: Int,
        index: Int,
        soundUri: String?
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.setPackage(context.packageName)
        intent.putExtra(AlarmExtraSpec.KEY_MEDICINE_NAME, medicineName)
        intent.putExtra(AlarmExtraSpec.KEY_DOSAGE_PER_TAKE, dosagePerTake)
        intent.putExtra(AlarmExtraSpec.KEY_DAILY_FREQUENCY, dailyFrequency)
        intent.putExtra(AlarmExtraSpec.KEY_DURATION_DAYS, durationDays)
        intent.putExtra(AlarmExtraSpec.KEY_SOUND_URI, soundUri)
        return PendingIntent.getBroadcast(
            context,
            generateRequestCode(medicineName, index),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
