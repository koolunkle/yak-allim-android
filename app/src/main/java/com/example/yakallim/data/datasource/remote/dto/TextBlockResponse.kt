package com.example.yakallim.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextBlockResponse(
    @SerialName("text") val text: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("bounds") val bounds: List<Coordinate> = emptyList()
)
