package com.example.yakallim.domain.model

enum class JobStatus {
    ENQUEUED,
    IMAGE_PROCESSING,
    TEXT_DETECTION,
    TEXT_RECOGNITION,
    EXPORT_RESULT,
    COMPLETED,
    FAILED;

    val isFinished: Boolean get() = this == COMPLETED || this == FAILED
}
