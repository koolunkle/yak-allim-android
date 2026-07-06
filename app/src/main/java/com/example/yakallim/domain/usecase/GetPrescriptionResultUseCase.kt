package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.model.Prescription
import com.example.yakallim.domain.repository.OcrRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrescriptionResultUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    operator fun invoke(jobId: String): Flow<Result<Prescription>> {
        return ocrRepository.fetchPrescriptionResult(jobId)
    }
}
