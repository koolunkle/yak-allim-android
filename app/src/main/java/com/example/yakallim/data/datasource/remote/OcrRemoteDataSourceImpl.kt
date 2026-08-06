package com.example.yakallim.data.datasource.remote

import com.example.yakallim.BuildConfig
import com.example.yakallim.data.datasource.remote.api.OcrApiService
import com.example.yakallim.data.datasource.remote.dto.OcrJobResponse
import com.example.yakallim.data.datasource.remote.dto.OcrProgressResponse
import com.example.yakallim.di.SseClient
import com.example.yakallim.domain.model.OcrJobStatus
import com.example.yakallim.domain.model.OcrProgress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.File
import javax.inject.Inject

class OcrRemoteDataSourceImpl @Inject constructor(
    private val apiService: OcrApiService,
    private val json: Json,
    @param:SseClient private val okHttpClient: OkHttpClient
) : OcrRemoteDataSource {

    override suspend fun enqueueOcrJob(imageFile: File, fcmToken: String?): OcrJobResponse {
        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
        val fcmTokenBody = fcmToken?.toRequestBody("text/plain".toMediaTypeOrNull())

        return apiService.enqueueOcrJob(imagePart, fcmTokenBody)
    }

    override suspend fun getOcrJob(jobId: String): OcrJobResponse {
        return apiService.getOcrJob(jobId)
    }

    override suspend fun cancelOcrJob(jobId: String) {
        apiService.cancelOcrJob(jobId)
    }

    override fun observeOcrProgress(jobId: String): Flow<OcrProgress> = callbackFlow {
        val request = okhttp3.Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/ocr/jobs/$jobId/progress")
            .header("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        val eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                if (type == null || type == "progress" || type == "message") {
                    try {
                        val progressResponse = json.decodeFromString<OcrProgressResponse>(data)
                        val stepStr = progressResponse.step ?: ""
                        val domainJobStatus = try {
                            OcrJobStatus.valueOf(stepStr)
                        } catch (_: IllegalArgumentException) {
                            OcrJobStatus.FAILED
                        }
                        val isFinished = progressResponse.isFinished
                        trySend(
                            OcrProgress(
                                jobStatus = domainJobStatus,
                                message = progressResponse.message ?: "",
                                percent = progressResponse.progress ?: 0,
                                isFinished = isFinished
                            )
                        )
                    } catch (_: Exception) {
                    }
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                close(t ?: RuntimeException("SSE connection failed"))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })

        awaitClose {
            eventSource.cancel()
        }
    }
}
