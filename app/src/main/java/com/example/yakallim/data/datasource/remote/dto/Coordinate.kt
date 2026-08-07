package com.example.yakallim.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coordinate(
    @SerialName("x") val x: Int,
    @SerialName("y") val y: Int
)
