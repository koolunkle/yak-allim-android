package com.example.yakallim.domain.model

data class Prescription(
    val rawText: String,
    val medications: List<Medication> = emptyList(),
    val textBlocks: List<TextBlock> = emptyList()
)
