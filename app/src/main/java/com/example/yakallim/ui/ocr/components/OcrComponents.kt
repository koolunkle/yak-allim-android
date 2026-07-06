package com.example.yakallim.ui.ocr.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yakallim.R
import com.example.yakallim.ui.ocr.OcrError
import com.example.yakallim.ui.theme.HighlightCoral
import com.example.yakallim.ui.theme.Primary
import com.example.yakallim.ui.theme.Secondary

@Composable
fun OcrLoadingContent(
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId = R.raw.pill))
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5f
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = lottieComposition,
            progress = { lottieProgress },
            modifier = Modifier.size(size = 240.dp)
        )
        Spacer(modifier = Modifier.height(height = 32.dp))
        Text(
            text = stringResource(R.string.ocr_status_analyzing),
            color = Primary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Text(
            text = stringResource(R.string.ocr_status_subtext),
            modifier = Modifier.padding(horizontal = 40.dp),
            color = Primary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(height = 48.dp))
        TextButton(
            onClick = onCancelClick,
            modifier = Modifier.height(height = 48.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = HighlightCoral.copy(alpha = 0.8f))
        ) {
            Text(
                text = stringResource(R.string.ocr_btn_cancel),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun OcrTopBar(onGuideButtonClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 48.dp)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.align(alignment = Alignment.Center),
                color = Primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = onGuideButtonClick,
                modifier = Modifier.align(alignment = Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = stringResource(R.string.help_title_guide),
                    tint = Primary
                )
            }
        }
    }
}

@Composable
fun OcrActionButton(
    isLoading: Boolean,
    enabled: Boolean,
    hasError: Boolean,
    onStartAnalysisClick: () -> Unit
) {
    if (!isLoading) {
        Button(
            onClick = onStartAnalysisClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(size = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Secondary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(if (hasError) R.string.ocr_btn_retry else R.string.ocr_btn_start),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OcrSourcePicker(
    onCameraCaptureClick: () -> Unit,
    onGallerySelectClick: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        OutlinedButton(
            onClick = onCameraCaptureClick,
            modifier = Modifier
                .weight(weight = 1f)
                .height(height = 48.dp),
            enabled = isEnabled,
            shape = RoundedCornerShape(size = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary),
            border = BorderStroke(width = 1.dp, color = Secondary.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(size = 20.dp)
            )
            Spacer(modifier = Modifier.width(width = 8.dp))
            Text(
                text = stringResource(R.string.camera_btn_capture),
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(
            onClick = onGallerySelectClick,
            modifier = Modifier
                .weight(weight = 1f)
                .height(height = 48.dp),
            enabled = isEnabled,
            shape = RoundedCornerShape(size = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary),
            border = BorderStroke(width = 1.dp, color = Secondary.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(size = 20.dp)
            )
            Spacer(modifier = Modifier.width(width = 8.dp))
            Text(
                text = stringResource(R.string.ocr_btn_gallery_select),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OcrErrorCard(error: OcrError) {
    val displayMessage = when (error) {
        is OcrError.Network -> stringResource(R.string.error_network)
        is OcrError.Timeout -> stringResource(R.string.error_timeout)
        is OcrError.EmptyResult -> stringResource(R.string.error_failed_parsing)
        is OcrError.AnalysisFailed -> stringResource(R.string.error_failed_analysis)
        is OcrError.ServerError -> error.message
        is OcrError.Unknown -> stringResource(R.string.error_failed_proceed, error.message)
    }
    val isNetworkError = error is OcrError.Network

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isNetworkError) Icons.Default.WifiOff else Icons.Default.Warning,
                contentDescription = stringResource(R.string.error_cd_icon),
                modifier = Modifier.size(size = 20.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = displayMessage,
                modifier = Modifier.weight(weight = 1f),
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun OcrResetButton(
    onResetAnalysisClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = onResetAnalysisClick,
            modifier = Modifier
                .height(height = 44.dp)
                .clip(shape = CircleShape),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary),
            border = BorderStroke(width = 1.dp, color = Secondary.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(size = 18.dp)
            )
            Spacer(modifier = Modifier.width(width = 8.dp))
            Text(
                text = stringResource(R.string.ocr_btn_image_change),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun OcrExpansionControls(
    isAllExpanded: Boolean,
    onToggleAllCardsExpansion: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { onToggleAllCardsExpansion(!isAllExpanded) },
            colors = ButtonDefaults.textButtonColors(contentColor = Secondary),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (isAllExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(size = 18.dp)
            )
            Spacer(modifier = Modifier.width(width = 6.dp))
            Text(
                text = stringResource(if (isAllExpanded) R.string.ocr_btn_image_collapse else R.string.ocr_btn_image_expand),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
