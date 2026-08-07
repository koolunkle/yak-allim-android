package com.example.yakallim.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolygonResponse(
    @SerialName("points") val points: List<Coordinate> = emptyList()
)
