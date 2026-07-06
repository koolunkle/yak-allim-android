package com.example.yakallim.data.repository

import android.content.Context
import android.util.Log
import com.example.yakallim.data.datasource.local.OcrLocalDataSource
import com.example.yakallim.data.datasource.remote.api.OcrApiService
import com.example.yakallim.data.mapper.toDomain
import com.example.yakallim.domain.infrastructure.fcm.FirebaseMessagingTokenProvider
import com.example.yakallim.domain.infrastructure.image.ImageProcessor
import com.example.yakallim.domain.model.Prescription
import com.example.yakallim.domain.repository.OcrRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import com.squareup.moshi.Moshi
import com.example.yakallim.data.datasource.remote.dto.OcrResponse

class OcrRepositoryImpl @Inject constructor(
    private val apiService: OcrApiService,
    private val imageProcessor: ImageProcessor,
    private val firebaseMessagingTokenProvider: FirebaseMessagingTokenProvider,
    private val ocrLocalDataSource: OcrLocalDataSource,
    private val moshi: Moshi,
    @param:ApplicationContext private val context: Context
) : OcrRepository {

    override suspend fun requestPrescription(imageFile: File): String =
        withContext(Dispatchers.IO) {
            val processedFile = imageProcessor.preprocess(imageFile)
            val imagePart = prepareImagePart(processedFile)

            val fcmToken = firebaseMessagingTokenProvider.getFcmToken()
            val fcmTokenBody = fcmToken?.toRequestBody("text/plain".toMediaTypeOrNull())

            val jobId = apiService.enqueueOcrJob(imagePart, fcmTokenBody).jobId
            ocrLocalDataSource.savePendingJobId(jobId)
            cacheProcessedImage(processedFile, jobId)
            jobId
        }

    override fun fetchPrescriptionResult(jobId: String): Flow<Result<Prescription>> = flow {
        try {
            val ocrJob = apiService.getOcrJob(jobId)
            val ocrJobResult = ocrJob.result
                ?: throw NoSuchElementException("OCR job [${ocrJob.jobId}] completed, but the result data is missing.")
            
            try {
                val adapter = moshi.adapter(OcrResponse::class.java)
                val json = adapter.toJson(ocrJobResult)
                ocrLocalDataSource.saveLastPrescriptionJson(json)
                val srcCache = File(context.cacheDir, "ocr_image_$jobId.jpg")
                if (srcCache.exists()) {
                    val dstCache = File(context.cacheDir, "ocr_image_last.jpg")
                    srcCache.copyTo(dstCache, overwrite = true)
                }
            } catch (e: Exception) {
                Log.e("OcrRepositoryImpl", "마지막 분석 결과 저장 실패: ${e.message}")
            }

            ocrLocalDataSource.clearPendingJobId()
            emit(Result.success(ocrJobResult.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun cancelPrescription() {
        ocrLocalDataSource.getPendingJobId()?.let { jobId ->
            ocrLocalDataSource.setAnalysisCancelled(jobId)
            try {
                apiService.cancelOcrJob(jobId)
            } catch (e: Exception) {
                Log.e("OcrRepositoryImpl", "작업 취소 API 호출 실패: ${e.message}")
            } finally {
                ocrLocalDataSource.clearPendingJobId()
            }
        }
    }

    override suspend fun getPendingPrescriptionJobId(): String? {
        return ocrLocalDataSource.getPendingJobId()
    }

    private fun prepareImagePart(file: File): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    private fun cacheProcessedImage(file: File, jobId: String) {
        try {
            val cacheFile = File(context.cacheDir, "ocr_image_$jobId.jpg")
            file.copyTo(cacheFile, overwrite = true)
        } catch (e: Exception) {
            Log.e("OcrRepositoryImpl", "이미지 캐싱 실패", e)
        }
    }

    override suspend fun getLastPrescription(): Prescription? {
        val json = ocrLocalDataSource.getLastPrescriptionJson() ?: return null
        return try {
            val adapter = moshi.adapter(OcrResponse::class.java)
            val ocrJobResult = adapter.fromJson(json)
            ocrJobResult?.toDomain()
        } catch (e: Exception) {
            Log.e("OcrRepositoryImpl", "마지막 분석 결과 복원 실패: ${e.message}")
            null
        }
    }

    override suspend fun clearLastPrescription() {
        ocrLocalDataSource.clearLastPrescriptionJson()
        try {
            val lastCache = File(context.cacheDir, "ocr_image_last.jpg")
            if (lastCache.exists()) {
                lastCache.delete()
            }
        } catch (e: Exception) {
            Log.e("OcrRepositoryImpl", "마지막 캐시 이미지 삭제 실패: ${e.message}")
        }
    }
}
