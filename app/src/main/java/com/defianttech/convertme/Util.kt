package com.defianttech.convertme

import android.app.PendingIntent
import android.os.Build
import android.text.Spanned
import androidx.core.text.HtmlCompat

object Util {

    fun fromHtml(text: String): Spanned {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getPendingIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else 0
    }

}
