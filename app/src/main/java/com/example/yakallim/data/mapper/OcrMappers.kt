package com.example.yakallim.data.mapper

import com.example.yakallim.data.datasource.remote.dto.OcrResponse
import com.example.yakallim.domain.model.Coordinate
import com.example.yakallim.domain.model.Medicine
import com.example.yakallim.domain.model.Prescription
import com.example.yakallim.domain.model.TextBlock

fun OcrResponse.toDomain(): Prescription {
    val textBlocks = this.textBlocks ?: emptyList()
    val rawText = textBlocks.joinToString(separator = "\n") { it.text }

    val mappedMedicines = this.prescriptions?.map { prescription ->
        val matchedTextBlock = textBlocks.find { it.text.contains(prescription.medicineName ?: "", ignoreCase = true) }
        val matchedConfidence = matchedTextBlock?.confidence ?: 1.0f

        Medicine(
            name = prescription.medicineName,
            dosagePerTake = prescription.dosagePerTake ?: "1",
            dailyFrequency = prescription.dailyFrequency ?: 0,
            durationDays = prescription.durationDays ?: 0,
            isLowConfidence = matchedConfidence < 0.8f,
            bounds = prescription.bounds?.map { polygon ->
                polygon.points.map { coordinate -> Coordinate(coordinate.x, coordinate.y) }
            } ?: emptyList()
        )
    } ?: emptyList()

    val mappedTextBlocks = textBlocks.map { textBlock ->
        TextBlock(
            text = textBlock.text,
            confidence = textBlock.confidence,
            bounds = textBlock.bounds.map { Coordinate(it.x, it.y) }
        )
    }

    return Prescription(
        rawText = rawText,
        medicines = mappedMedicines,
        textBlocks = mappedTextBlocks
    )
}
