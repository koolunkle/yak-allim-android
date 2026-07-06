package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(
        medicineName: String,
        dosagePerTake: String,
        dailyFrequency: Int,
        durationDays: Int,
        instruction: String
    ) {
        alarmScheduler.schedule(medicineName, dosagePerTake, dailyFrequency, durationDays, instruction)
    }
}
