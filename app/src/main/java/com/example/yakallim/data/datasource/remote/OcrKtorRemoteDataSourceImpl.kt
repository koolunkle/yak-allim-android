package com.example.yakallim.data.datasource.remote

import com.example.yakallim.BuildConfig
import com.example.yakallim.data.datasource.remote.dto.OcrJobResponse
import com.example.yakallim.data.datasource.remote.dto.OcrProgressResponse
import com.example.yakallim.di.SseClient
import com.example.yakallim.domain.model.OcrJobStatus
import com.example.yakallim.domain.model.OcrProgress
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.File
import javax.inject.Inject

class OcrKtorRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    @param:SseClient private val sseOkHttpClient: OkHttpClient
) : OcrRemoteDataSource {

    override suspend fun enqueueOcrJob(imageFile: File, fcmToken: String?): OcrJobResponse {
        val contentType = when (imageFile.extension.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }

        return httpClient.post("${BuildConfig.BASE_URL}api/v1/ocr/enqueue") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "file",
                            value = imageFile.readBytes(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
                            }
                        )
                        if (!fcmToken.isNullOrBlank()) {
                            append(key = "fcmToken", value = fcmToken)
                        }
                    }
                )
            )
        }.body()
    }

    override suspend fun getOcrJob(jobId: String): OcrJobResponse {
        return httpClient.get("${BuildConfig.BASE_URL}api/v1/ocr/jobs/$jobId").body()
    }

    override suspend fun cancelOcrJob(jobId: String) {
        httpClient.post("${BuildConfig.BASE_URL}api/v1/ocr/jobs/$jobId/cancel")
    }

    override fun observeOcrProgress(jobId: String): Flow<OcrProgress> = callbackFlow {
        val request = okhttp3.Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/ocr/jobs/$jobId/progress")
            .header("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(sseOkHttpClient)
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
