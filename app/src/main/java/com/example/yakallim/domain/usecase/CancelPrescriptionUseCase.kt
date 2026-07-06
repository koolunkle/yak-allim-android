package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.repository.OcrRepository
import javax.inject.Inject

class CancelPrescriptionUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    suspend operator fun invoke() {
        ocrRepository.cancelPrescription()
    }
}
