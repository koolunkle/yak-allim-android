package com.example.yakallim.domain.infrastructure.alarm

interface AlarmScheduler {
    suspend fun schedule(
        medicineName: String,
        dosagePerTake: String,
        dailyFrequency: Int,
        durationDays: Int,
        instruction: String
    )
    suspend fun cancel(medicineName: String)

    suspend fun getActiveAlarm(): Set<String>
}