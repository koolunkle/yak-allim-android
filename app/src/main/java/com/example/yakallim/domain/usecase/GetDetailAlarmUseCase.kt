package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import com.example.yakallim.domain.model.Alarm
import javax.inject.Inject

class GetDetailAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(medicineName: String): Alarm? {
        return alarmScheduler.getDetailAlarm(medicineName)
    }
}
