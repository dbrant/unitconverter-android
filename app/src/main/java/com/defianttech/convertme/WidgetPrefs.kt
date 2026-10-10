package com.defianttech.convertme

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.content.edit

class WidgetPrefs(context: Context, private val widgetId: Int) {
    var currentCategory: Int
    var currentFromIndex: Int
    var currentToIndex: Int
    var currentValue: Float
    var increment: Float

    fun save(context: Context) {
        ConvertActivity.getPrefs(context).edit {
            putInt("widget_category_$widgetId", currentCategory)
            putInt("widget_from_$widgetId", currentFromIndex)
            putInt("widget_to_$widgetId", currentToIndex)
            putFloat("widget_increment_$widgetId", increment)
            putFloat("widget_from_value_$widgetId", currentValue)
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
        increment = prefs.getFloat("widget_increment_$widgetId", 1f)
        currentValue = prefs.getFloat("widget_from_value_$widgetId", 1f)
    }
}
