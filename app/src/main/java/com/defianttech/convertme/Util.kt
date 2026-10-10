package com.defianttech.convertme

import android.graphics.Color
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative

object Util {
    fun fromHtml(text: String): Spanned {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    /**
     * Lets the activity draw behind the system bars. The system bar icons are always light, since
     * the toolbar and the window background are dark regardless of the system theme.
     */
    fun enableEdgeToEdge(activity: ComponentActivity) {
        activity.enableEdgeToEdge(SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT))
    }

    /**
     * Returns the insets that the contents of the given view need to stay clear of, i.e. the system
     * bars, display cutouts, and keyboard. For convenience with right-to-left layouts, the returned
     * left and right insets are actually the start and end insets.
     */
    fun getRelativeInsets(view: View, windowInsets: WindowInsetsCompat): Insets {
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
        return if (view.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            Insets.of(insets.right, insets.top, insets.left, insets.bottom)
        } else {
            insets
        }
    }

    /**
     * Keeps the contents of the given view clear of the system bars (see [getRelativeInsets]) on the
     * given sides, by adding the corresponding insets to its padding, which lets its background
     * extend behind them. If the view has a fixed height, it grows by the added padding.
     * If [asMargin] is true, the insets are added to the view's margins instead.
     */
    fun applyWindowInsets(view: View, start: Boolean = false, top: Boolean = false, end: Boolean = false,
                          bottom: Boolean = false, asMargin: Boolean = false) {
        val initialPaddingStart = view.paddingStart
        val initialPaddingTop = view.paddingTop
        val initialPaddingEnd = view.paddingEnd
        val initialPaddingBottom = view.paddingBottom
        val initialHeight = view.layoutParams.height
        val initialMargins = view.layoutParams as? ViewGroup.MarginLayoutParams
        val initialMarginStart = initialMargins?.marginStart ?: 0
        val initialMarginTop = initialMargins?.topMargin ?: 0
        val initialMarginEnd = initialMargins?.marginEnd ?: 0
        val initialMarginBottom = initialMargins?.bottomMargin ?: 0

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = getRelativeInsets(v, windowInsets)
            val insetStart = if (start) insets.left else 0
            val insetTop = if (top) insets.top else 0
            val insetEnd = if (end) insets.right else 0
            val insetBottom = if (bottom) insets.bottom else 0
            if (asMargin) {
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = initialMarginStart + insetStart
                    topMargin = initialMarginTop + insetTop
                    marginEnd = initialMarginEnd + insetEnd
                    bottomMargin = initialMarginBottom + insetBottom
                }
            } else {
                v.updatePaddingRelative(initialPaddingStart + insetStart, initialPaddingTop + insetTop,
                        initialPaddingEnd + insetEnd, initialPaddingBottom + insetBottom)
                if (initialHeight > 0) {
                    v.updateLayoutParams { height = initialHeight + insetTop + insetBottom }
                }
            }
            windowInsets
        }
    }
}
