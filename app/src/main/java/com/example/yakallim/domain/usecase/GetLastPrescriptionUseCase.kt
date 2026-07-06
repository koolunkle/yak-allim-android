package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.model.Prescription
import com.example.yakallim.domain.repository.OcrRepository
import javax.inject.Inject

class GetLastPrescriptionUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    suspend operator fun invoke(): Prescription? = ocrRepository.getLastPrescription()
}
