package com.example.yakallim.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("fileName") val fileName: String,
    @SerialName("message") val message: String,
    @SerialName("textBlocks") val textBlocks: List<TextBlockResponse>? = emptyList(),
    @SerialName("medicines") val medicines: List<MedicineResponse>? = emptyList()
)
