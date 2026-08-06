package com.example.yakallim.data.datasource.remote

import com.example.yakallim.data.datasource.remote.dto.OcrJobResponse
import com.example.yakallim.domain.model.OcrProgress
import kotlinx.coroutines.flow.Flow
import java.io.File

interface OcrRemoteDataSource {
    suspend fun enqueueOcrJob(imageFile: File, fcmToken: String?): OcrJobResponse
    suspend fun getOcrJob(jobId: String): OcrJobResponse
    suspend fun cancelOcrJob(jobId: String)
    fun observeOcrProgress(jobId: String): Flow<OcrProgress>
}
