package com.example.yakallim.ui.ocr

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yakallim.R
import com.example.yakallim.ui.camera.CameraScreen
import com.example.yakallim.ui.ocr.components.OcrActionButton
import com.example.yakallim.ui.ocr.components.OcrErrorCard
import com.example.yakallim.ui.ocr.components.OcrExpansionControls
import com.example.yakallim.ui.ocr.components.OcrGuideBottomSheet
import com.example.yakallim.ui.ocr.components.OcrImageViewer
import com.example.yakallim.ui.ocr.components.OcrLoadingContent
import com.example.yakallim.ui.ocr.components.OcrMedicationCard
import com.example.yakallim.ui.ocr.components.OcrResetButton
import com.example.yakallim.ui.ocr.components.OcrSourcePicker
import com.example.yakallim.ui.ocr.components.OcrTopBar
import com.example.yakallim.util.hasExactAlarmPermission
import com.example.yakallim.util.hasNotificationPermission
import com.example.yakallim.util.hasPermission
import com.example.yakallim.util.openExactAlarmSettings
import com.example.yakallim.util.requiredNotificationPermission
import com.example.yakallim.util.showToast
import kotlinx.coroutines.launch

@Composable
fun OcrScreen(
    modifier: Modifier = Modifier,
    viewModel: OcrViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsState()

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val highlightedMedicineName = remember { mutableStateOf<String?>(null) }
    val showCamera = remember { mutableStateOf(false) }
    val showGuideSheet = remember { mutableStateOf(false) }

    val unknownMedicineLabel = stringResource(R.string.error_unknown_medicine)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onAppForeground()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) context.showToast(R.string.alarm_permission_denied)
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) showCamera.value =
                true else context.showToast(R.string.camera_permission_required)
        }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { viewModel.onImageSelected(it) }
    }

    if (showCamera.value) {
        CameraScreen(
            onCaptureSuccess = { viewModel.onImageCaptured(it); showCamera.value = false },
            onDismiss = { showCamera.value = false }
        )
        return
    }

    val onRegisterAlarm =
        { medicineName: String, dosagePerTake: String, dailyFrequency: Int, durationDays: Int, instruction: String ->
            checkPermissions(
                context,
                { requiredNotificationPermission?.let { notificationLauncher.launch(it) } }) {
                viewModel.registerMedicineAlarm(
                    medicineName,
                    dosagePerTake,
                    dailyFrequency,
                    durationDays,
                    instruction
                )
            }
        }

    OcrScreenContent(
        modifier = modifier,
        uiState = uiState,
        lazyListState = lazyListState,
        highlightedMedicineName = highlightedMedicineName.value,
        isGuideSheetVisible = showGuideSheet.value,
        unknownMedicineFallbackText = unknownMedicineLabel,
        onGuideClick = { showGuideSheet.value = true },
        onGuideDismissRequest = { showGuideSheet.value = false },
        onCameraCaptureClick = {
            if (context.hasPermission(Manifest.permission.CAMERA)) {
                showCamera.value = true
            } else {
                cameraLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onGallerySelectClick = { galleryLauncher.launch("image/*") },
        onStartAnalysisClick = viewModel::retryAnalysis,
        onCancelAnalysisClick = viewModel::onAnalysisCancelRequested,
        onRegisterAlarmClick = onRegisterAlarm,
        onCancelAlarmClick = viewModel::unregisterMedicineAlarm,
        onResetAnalysisClick = viewModel::resetAnalysisResult,
        onMedicineTextClick = { name ->
            val result = uiState.analysisResult
            val medicines = result?.medicines ?: emptyList()
            val matchedMedication =
                medicines.find { (it.name ?: unknownMedicineLabel) == name }
            highlightedMedicineName.value = matchedMedication?.name ?: unknownMedicineLabel

            val index = medicines.indexOf(matchedMedication)
            if (index != -1) {
                coroutineScope.launch {
                    val hasResult = result != null && result.medicines.isNotEmpty()
                    var target = 1
                    if (!hasResult || uiState.isLoading) target += 2
                    if (uiState.error != null) target += 1
                    if (hasResult) target += 1
                    val offset = with(density) { (-16).dp.roundToPx() }
                    lazyListState.animateScrollToItem(target + index, offset)
                }
            }
        },
        onToggleCardExpansion = viewModel::toggleCardExpansion,
        onToggleAllCardsExpansion = viewModel::setAllCardsExpansion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OcrScreenContent(
    modifier: Modifier,
    uiState: OcrUiState,
    lazyListState: LazyListState,
    highlightedMedicineName: String?,
    isGuideSheetVisible: Boolean,
    unknownMedicineFallbackText: String,
    onGuideClick: () -> Unit,
    onGuideDismissRequest: () -> Unit,
    onCameraCaptureClick: () -> Unit,
    onGallerySelectClick: () -> Unit,
    onResetAnalysisClick: () -> Unit,
    onStartAnalysisClick: () -> Unit,
    onCancelAnalysisClick: () -> Unit,
    onMedicineTextClick: (String) -> Unit,
    onRegisterAlarmClick: (String, String, Int, Int, String) -> Unit,
    onCancelAlarmClick: (String) -> Unit,
    onToggleCardExpansion: (String) -> Unit,
    onToggleAllCardsExpansion: (Boolean) -> Unit
) {
    Scaffold(
        topBar = { OcrTopBar(onGuideButtonClick = onGuideClick) },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isGuideSheetVisible) OcrGuideBottomSheet(onDismiss = onGuideDismissRequest)

        if (!uiState.isInitialized) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        val hasResult = uiState.analysisResult != null && uiState.analysisResult.medicines.isNotEmpty()
        val isAllExpanded = uiState.cardExpansionMap.values.all { it } && uiState.cardExpansionMap.isNotEmpty()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                if (uiState.isLoading) {
                    val progressState = uiState.progressState ?: OcrProgressState()
                    OcrLoadingContent(
                        progress = progressState.progress,
                        message = progressState.message,
                        isSseActive = progressState.isSseActive,
                        onCancelClick = onCancelAnalysisClick
                    )
                } else {
                    OcrImageViewer(
                        selectedImageUri = uiState.selectedImageUri,
                        capturedImageBitmap = uiState.capturedImageBitmap,
                        analysisResult = uiState.analysisResult,
                        registeredAlarmMedicineNames = uiState.registeredAlarmMedicineNames,
                        onMedicineTextClick = onMedicineTextClick
                    )
                }
            }

            if (!hasResult && !uiState.isLoading) {
                item { OcrSourcePicker(onCameraCaptureClick, onGallerySelectClick, true) }
                item {
                OcrActionButton(
                    isLoading = false,
                    enabled = uiState.hasImage || uiState.error != null,
                    hasError = uiState.error != null,
                    onStartAnalysisClick = onStartAnalysisClick
                )
                }
            }

            uiState.error?.let { item { OcrErrorCard(it) } }

            uiState.analysisResult?.let { result ->
                if (result.medicines.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OcrResetButton(onResetAnalysisClick)
                            OcrExpansionControls(isAllExpanded, onToggleAllCardsExpansion)
                        }
                    }
                    itemsIndexed(
                        result.medicines,
                        { _, med -> med.name ?: med.hashCode() }) { _, med ->
                        val name = med.name ?: unknownMedicineFallbackText
                        OcrMedicationCard(
                            medicineInfo = med,
                            isAlarmRegistered = uiState.registeredAlarmMedicineNames.contains(name),
                            highlightedMedicineName = highlightedMedicineName,
                            isCardExpanded = uiState.cardExpansionMap[name] ?: true,
                            onToggleExpansionClick = { onToggleCardExpansion(name) },
                            onRegisterAlarmClick = onRegisterAlarmClick,
                            onCancelAlarmClick = onCancelAlarmClick
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

private fun checkPermissions(
    context: Context,
    launchNotification: () -> Unit,
    onGranted: () -> Unit
) {
    if (!context.hasNotificationPermission()) {
        launchNotification(); return
    }
    if (!context.hasExactAlarmPermission()) {
        context.showToast(
            R.string.alarm_permission_required,
            Toast.LENGTH_LONG
        ); context.openExactAlarmSettings(); return
    }
    onGranted()
}
