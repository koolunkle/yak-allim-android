package com.example.yakallim.ui.ocr.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yakallim.R
import com.example.yakallim.domain.model.Coordinate
import com.example.yakallim.domain.model.Prescription
import com.example.yakallim.ui.ocr.OcrImage
import com.example.yakallim.ui.theme.Primary
import com.example.yakallim.ui.theme.Secondary
import com.example.yakallim.ui.theme.Success
import com.example.yakallim.ui.theme.Surface

@Composable
fun OcrImageViewer(
    image: OcrImage?,
    analysisResult: Prescription?,
    registeredAlarmMedicineNames: Set<String> = emptySet(),
    onMedicineTextClick: (String) -> Unit
) {
    val unknownMedicineLabel = stringResource(R.string.error_unknown_medicine)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 300.dp)
            .clip(RoundedCornerShape(size = 24.dp))
            .background(color = Surface)
            .border(
                width = 1.dp,
                color = Primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(size = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (image) {
                    is OcrImage.UriSource -> AsyncImage(
                        model = image.uri,
                        contentDescription = stringResource(R.string.ocr_cd_prescription),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    is OcrImage.BitmapSource -> Image(
                        bitmap = image.bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.camera_cd_prescription),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                val context = LocalContext.current
                val origSize = remember(image) {
                    when (image) {
                        is OcrImage.BitmapSource -> Pair(image.bitmap.width, image.bitmap.height)
                        is OcrImage.UriSource -> {
                            try {
                                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                context.contentResolver.openInputStream(image.uri)?.use { BitmapFactory.decodeStream(it, null, options) }
                                if (options.outWidth > 0) Pair(options.outWidth, options.outHeight) else Pair(1000, 1000)
                            } catch (e: Exception) {
                                Log.e("OcrImageViewer", "Size failed", e); Pair(1000, 1000)
                            }
                        }
                    }
                }

                val medicines = analysisResult?.medicines ?: emptyList()
                if (medicines.any { it.bounds.isNotEmpty() }) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val density = LocalDensity.current
                        val viewW = with(density) { maxWidth.toPx() }
                        val viewH = with(density) { maxHeight.toPx() }

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(analysisResult) {
                                    detectTapGestures { offset ->
                                        medicines.forEach { medicine ->
                                            val medicineName =
                                                medicine.name ?: unknownMedicineLabel
                                            medicine.bounds.forEach { box ->
                                                if (getScaledRect(
                                                        box.points,
                                                        origSize.first,
                                                        origSize.second,
                                                        viewW,
                                                        viewH
                                                    ).inflate(delta = 8.dp.toPx())
                                                        .contains(offset = offset)
                                                ) {
                                                    onMedicineTextClick(medicineName); return@detectTapGestures
                                                }
                                            }
                                        }
                                    }
                                }) {
                            medicines.forEach { medicine ->
                                val medicineName = medicine.name ?: unknownMedicineLabel
                                val isRegistered = registeredAlarmMedicineNames.contains(medicineName)

                                val fillColor = if (isRegistered) Success.copy(0.2f) else Secondary.copy(0.15f)
                                val borderColor = if (isRegistered) Success.copy(0.6f) else Secondary.copy(0.4f)

                                medicine.bounds.forEach { box ->
                                    val rect = getScaledRect(box.points, origSize.first, origSize.second, viewW, viewH)
                                    drawRect(color = fillColor, topLeft = rect.topLeft, size = rect.size)
                                    drawRect(color = borderColor, topLeft = rect.topLeft, size = rect.size, style = Stroke(width = 1.5f.dp.toPx()))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = stringResource(R.string.ocr_cd_upload_icon),
                    modifier = Modifier.size(size = 64.dp),
                    tint = Primary.copy(alpha = 0.15f)
                )
                Text(
                    text = stringResource(R.string.ocr_status_placeholder),
                    color = Primary.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun getScaledRect(coordinates: List<Coordinate>, origW: Int, origH: Int, viewW: Float, viewH: Float): Rect {
    val minX = coordinates.minOfOrNull { it.x } ?: 0
    val maxX = coordinates.maxOfOrNull { it.x } ?: 0
    val minY = coordinates.minOfOrNull { it.y } ?: 0
    val maxY = coordinates.maxOfOrNull { it.y } ?: 0

    if (origW <= 0) return Rect(
        left = minX.toFloat(),
        top = minY.toFloat(),
        right = maxX.toFloat(),
        bottom = maxY.toFloat()
    )

    val scale = maxOf(a = viewW / origW, b = viewH / origH)
    val offX = (viewW - origW * scale) / 2f
    val offY = (viewH - origH * scale) / 2f

    return Rect(
        left = offX + minX * scale,
        top = offY + minY * scale,
        right = offX + maxX * scale,
        bottom = offY + maxY * scale
    )
}
