package com.example.yakallim.domain.notification

import com.example.yakallim.domain.model.OcrJobStatus

data class PushMessage(
    val jobId: String,
    val status: OcrJobStatus,
    val errorMessage: String? = null
)
