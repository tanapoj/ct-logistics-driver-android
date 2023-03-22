package com.scgexpress.backoffice.android.common

import android.content.Context


fun Int.pxToDp(context: Context) = toFloat().pxToDp(context)
fun Float.pxToDp(context: Context) = this / context.resources.displayMetrics.density

fun Int.dpToPx(context: Context) = toFloat().dpToPx(context)
fun Float.dpToPx(context: Context) = this * context.resources.displayMetrics.density