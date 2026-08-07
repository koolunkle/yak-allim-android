package com.example.yakallim.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("fileName") val fileName: String,
    @SerialName("message") val message: String,
    @SerialName("textBlocks") val textBlocks: List<TextBlockResponse>? = emptyList(),
    @SerialName("medicines") val medicines: List<MedicineResponse>? = emptyList()
) {
    @Serializable
    data class Coordinate(
        @SerialName("x") val x: Int,
        @SerialName("y") val y: Int
    )

    @Serializable
    data class TextBlockResponse(
        @SerialName("text") val text: String,
        @SerialName("confidence") val confidence: Float,
        @SerialName("bounds") val bounds: List<Coordinate> = emptyList()
    )

    @Serializable
    data class Polygon(
        @SerialName("points") val points: List<Coordinate> = emptyList()
    )

    @Serializable
    data class MedicineResponse(
        @SerialName("medicineName") val medicineName: String?,
        @SerialName("dosagePerTake") val dosagePerTake: String? = "",
        @SerialName("dailyFrequency") val dailyFrequency: Int? = 0,
        @SerialName("durationDays") val durationDays: Int? = 0,
        @SerialName("bounds") val bounds: List<Polygon>? = emptyList()
    )
}
