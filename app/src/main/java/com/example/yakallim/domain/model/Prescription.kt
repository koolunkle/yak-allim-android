package com.example.yakallim.domain.model

data class Prescription(
    val rawText: String,
    val medicines: List<Medicine> = emptyList(),
    val textBlocks: List<TextBlock> = emptyList()
)
