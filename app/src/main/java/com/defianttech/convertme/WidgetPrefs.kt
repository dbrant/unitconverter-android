package com.defianttech.convertme

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import java.math.MathContext

class WidgetPrefs(context: Context, private val widgetId: Int) {
    var currentCategory: Int
    var currentFromIndex: Int
    var currentToIndex: Int
    var currentValue: Double
    var increment: Double

    fun save(context: Context) {
        ConvertActivity.getPrefs(context).edit {
            putInt("widget_category_$widgetId", currentCategory)
            putInt("widget_from_$widgetId", currentFromIndex)
            putInt("widget_to_$widgetId", currentToIndex)
            putString("widget_increment_$widgetId", increment.toString())
            putString("widget_from_value_$widgetId", currentValue.toString())
        }
        val updateIntent = Intent(context, WidgetProvider::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(widgetId)
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(updateIntent)
    }

    init {
        val prefs = ConvertActivity.getPrefs(context)
        currentCategory = prefs.getInt("widget_category_$widgetId", UnitCollection.DEFAULT_CATEGORY)
        currentFromIndex = prefs.getInt("widget_from_$widgetId", UnitCollection.DEFAULT_FROM_INDEX)
        currentToIndex = prefs.getInt("widget_to_$widgetId", UnitCollection.DEFAULT_TO_INDEX)
        increment = getDouble(prefs, "widget_increment_$widgetId", 1.0)
        currentValue = getDouble(prefs, "widget_from_value_$widgetId", 1.0)
    }

    /** Doubles are saved as strings, since SharedPreferences can't store them directly. Older versions
     * saved these values as Floats, which are converted by rounding to the 7 significant digits that a
     * Float can hold. This recovers the values that the user entered (0.1 instead of 0.10000000149011612),
     * and removes the error that accumulated when incrementing them (1.3 instead of 1.3000001). The next
     * save replaces them with strings under the same key. */
    private fun getDouble(prefs: SharedPreferences, key: String, defValue: Double): Double {
        return when (val value = prefs.all[key]) {
            is String -> value.toDoubleOrNull()?.takeIf { it.isFinite() } ?: defValue
            is Float -> if (value.isFinite()) value.toBigDecimal().round(MathContext(7)).toDouble() else defValue
            else -> defValue
        }
    }
}
