package com.example.yakallim.ui.ocr

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.example.yakallim.domain.model.Medicine
import com.example.yakallim.domain.model.Prescription
import org.junit.Rule
import org.junit.Test

class OcrScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun ocrScreen_withDuplicateMedicineNames_shouldNotCrash() {
        val duplicateMedicines = listOf(
            Medicine(
                name = "타이레놀",
                dosagePerTake = "1",
                dailyFrequency = 3,
                durationDays = 3
            ),
            Medicine(
                name = "타이레놀",
                dosagePerTake = "2",
                dailyFrequency = 2,
                durationDays = 5
            )
        )

        val mockUiState = OcrUiState(
            isInitialized = true,
            analysisResult = Prescription(
                rawText = "Dummy Raw Text",
                medicines = duplicateMedicines
            )
        )

        composeTestRule.setContent {
            OcrScreenContent(
                modifier = Modifier,
                uiState = mockUiState,
                lazyListState = rememberLazyListState(),
                highlightedMedicineName = null,
                isGuideSheetVisible = false,
                unknownMedicineLabel = "알 수 없는 약품",
                onGuideClick = {},
                onGuideDismissRequest = {},
                onCameraCaptureClick = {},
                onGallerySelectClick = {},
                onResetAnalysisClick = {},
                onStartAnalysisClick = {},
                onCancelAnalysisClick = {},
                onMedicineTextClick = {},
                onRegisterAlarmClick = { _, _, _, _ -> },
                onCancelAlarmClick = {},
                onToggleCardExpansion = {},
                onToggleAllCardsExpansion = {}
            )
        }
    }
}
