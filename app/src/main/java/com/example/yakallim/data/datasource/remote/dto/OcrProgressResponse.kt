package com.example.yakallim.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OcrProgressResponse(
    @field:Json(name = "step") val step: String?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "progress") val progress: Int?,
    @field:Json(name = "isFinished") val isFinished: Boolean?
)
