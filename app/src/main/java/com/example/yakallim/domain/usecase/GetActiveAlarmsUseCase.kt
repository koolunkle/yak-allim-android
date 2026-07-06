package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.infrastructure.alarm.AlarmScheduler
import javax.inject.Inject

class GetActiveAlarmsUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(): Set<String> = alarmScheduler.getActiveAlarm()
}
