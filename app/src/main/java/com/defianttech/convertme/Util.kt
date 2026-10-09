package com.defianttech.convertme

import android.text.Spanned
import androidx.core.text.HtmlCompat

object Util {
    fun fromHtml(text: String): Spanned {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
