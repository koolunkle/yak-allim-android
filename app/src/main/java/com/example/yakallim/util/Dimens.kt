package com.example.yakallim.util

import android.content.Context

val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels

fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
