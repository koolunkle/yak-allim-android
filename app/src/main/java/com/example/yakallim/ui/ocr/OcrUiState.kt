package com.example.yakallim.ui.ocr

import android.graphics.Bitmap
import android.net.Uri
import com.example.yakallim.domain.model.Prescription

sealed interface OcrError {
    object Network : OcrError
    object Timeout : OcrError
    object EmptyResult : OcrError
    object AnalysisFailed : OcrError
    data class ServerError(val message: String) : OcrError
    data class Unknown(val message: String) : OcrError
}

data class OcrUiState(
    val selectedImageUri: Uri? = null,
    val capturedImageBitmap: Bitmap? = null,
    val analysisResult: Prescription? = null,
    val registeredAlarmMedicineNames: Set<String> = emptySet(),
    val cardExpansionMap: Map<String, Boolean> = emptyMap(),
    val isLoading: Boolean = false,
    val isInitialized: Boolean = false,
    val error: OcrError? = null
) {
    val hasImage: Boolean get() = selectedImageUri != null || capturedImageBitmap != null
}
