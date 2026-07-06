package com.example.yakallim.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OcrJobResponse(
    @field:Json(name = "jobId") val jobId: String,
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "result") val result: OcrResponse? = null,
    @field:Json(name = "error") val error: String? = null
)