package com.example.yakallim.domain.model

data class Medication(
    val medicineName: String?,
    val dosagePerTake: String,
    val dailyFrequency: Int,
    val durationDays: Int,
    val bounds: List<List<TextBlock.Coordinate>> = emptyList(),
    val isLowConfidence: Boolean = false
)
