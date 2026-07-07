package com.example.yakallim.domain.model

data class Medicine(
    val name: String?,
    val dosagePerTake: String,
    val dailyFrequency: Int,
    val durationDays: Int,
    val bounds: List<BoundingPolygon> = emptyList(),
    val isLowConfidence: Boolean = false
)
