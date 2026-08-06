package com.example.yakallim.domain.model

data class OcrProgress(
    val jobStatus: OcrJobStatus,
    val message: String,
    val percent: Int,
    val isFinished: Boolean = jobStatus.isFinished
)
