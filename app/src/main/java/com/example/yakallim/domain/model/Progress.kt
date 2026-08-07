package com.example.yakallim.domain.model

data class Progress(
    val jobStatus: JobStatus,
    val message: String,
    val percent: Int,
    val isFinished: Boolean = jobStatus.isFinished
)
