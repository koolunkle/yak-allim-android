package com.example.yakallim.domain.model

data class TextBlock(
    val text: String,
    val confidence: Float,
    val bounds: List<Coordinate> = emptyList()
)
