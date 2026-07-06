package com.example.yakallim.domain.infrastructure.fcm

import com.example.yakallim.domain.model.JobStatus

data class FirebaseMessage(
    val jobId: String,
    val status: JobStatus,
    val errorMessage: String? = null
)
