package com.example.yakallim.domain.usecase

import com.example.yakallim.domain.model.Progress
import com.example.yakallim.domain.repository.OcrRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProgressUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    operator fun invoke(jobId: String): Flow<Progress> {
        return ocrRepository.observeOcrProgress(jobId)
    }
}
