package com.example.yakallim.domain.notification

import com.example.yakallim.domain.model.JobStatus

data class PushMessage(
    val jobId: String,
    val status: JobStatus,
    val errorMessage: String? = null
)
