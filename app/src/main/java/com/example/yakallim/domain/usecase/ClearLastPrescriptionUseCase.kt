package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.repository.OcrRepository
import javax.inject.Inject

class ClearLastPrescriptionUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    suspend operator fun invoke() = ocrRepository.clearLastPrescription()
}
