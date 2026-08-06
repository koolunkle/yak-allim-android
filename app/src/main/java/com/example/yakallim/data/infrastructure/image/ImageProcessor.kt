package com.example.yakallim.data.infrastructure.image

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface ImageProcessor {
    suspend fun preprocess(file: File): File
    suspend fun uriToFile(uri: Uri): File?
    suspend fun bitmapToFile(bitmap: Bitmap): File?
}
