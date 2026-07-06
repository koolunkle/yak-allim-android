package com.example.yakallim.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OcrResponse(
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "fileName") val fileName: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "textBlocks") val textBlocks: List<TextBlockResponse>? = emptyList(),
    @field:Json(name = "prescriptions") val prescriptions: List<PrescriptionResponse>? = emptyList()
) {
    @JsonClass(generateAdapter = true)
    data class Coordinate(
        @field:Json(name = "x") val x: Int,
        @field:Json(name = "y") val y: Int
    )

    @JsonClass(generateAdapter = true)
    data class TextBlockResponse(
        @field:Json(name = "text") val text: String,
        @field:Json(name = "confidence") val confidence: Float,
        @field:Json(name = "bounds") val bounds: List<Coordinate> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class Polygon(
        @field:Json(name = "points") val points: List<Coordinate> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class PrescriptionResponse(
        @field:Json(name = "medicineName") val medicineName: String?,
        @field:Json(name = "dosagePerTake") val dosagePerTake: String? = "",
        @field:Json(name = "dailyFrequency") val dailyFrequency: Int? = 0,
        @field:Json(name = "durationDays") val durationDays: Int? = 0,
        @field:Json(name = "bounds") val bounds: List<Polygon>? = emptyList()
    )
}
