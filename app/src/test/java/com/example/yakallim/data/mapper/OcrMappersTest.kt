package com.example.yakallim.data.mapper

import com.example.yakallim.data.datasource.remote.dto.OcrResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class OcrMappersTest {

    @Test
    fun toDomain_whenDosagePerTakeIsNull_mapsToFallbackValueOne() {
        val rawResponse = OcrResponse(
            fileName = "test.jpg",
            message = "Success",
            textBlocks = emptyList(),
            prescriptions = listOf(
                OcrResponse.PrescriptionResponse(
                    medicineName = "타이레놀",
                    dosagePerTake = null,
                    dailyFrequency = 3,
                    durationDays = 5,
                    bounds = null
                )
            )
        )

        val domainPrescription = rawResponse.toDomain()
        val mappedMedication = domainPrescription.medications.first()

        assertEquals("1", mappedMedication.dosagePerTake)
    }

    @Test
    fun toDomain_whenDosagePerTakeIsProvided_preservesValue() {
        val rawResponse = OcrResponse(
            fileName = "test_image.png",
            message = "Fetched successfully",
            textBlocks = emptyList(),
            prescriptions = listOf(
                OcrResponse.PrescriptionResponse(
                    medicineName = "아스피린",
                    dosagePerTake = "2정",
                    dailyFrequency = 2,
                    durationDays = 7,
                    bounds = null
                )
            )
        )

        val domainPrescription = rawResponse.toDomain()
        val mappedMedication = domainPrescription.medications.first()

        assertEquals("2정", mappedMedication.dosagePerTake)
    }
}
