package com.defianttech.convertme

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val thisWidget = ComponentName(context, WidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        val collections = UnitCollection.getInstance(context)

        for (widgetId in allWidgetIds) {
            Log.d(TAG, "Updating widget ID $widgetId")

            val remoteViews = RemoteViews(context.packageName, R.layout.widget_convert_units)
            remoteViews.setOnClickPendingIntent(R.id.widget_unit_plus, getSelfPendingIntent(context, widgetId, CLICK_ACTION_PLUS))
            remoteViews.setOnClickPendingIntent(R.id.widget_unit_minus, getSelfPendingIntent(context, widgetId, CLICK_ACTION_MINUS))
            remoteViews.setOnClickPendingIntent(R.id.widget_unit_container, getSelfPendingIntent(context, widgetId, CLICK_ACTION_EXCHANGE))

            val configIntent = Intent(context, WidgetSetupActivity::class.java)
            configIntent.action = CLICK_ACTION_SETTINGS + "_" + widgetId
            val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, Util.getPendingIntentFlags())

            remoteViews.setOnClickPendingIntent(R.id.widget_settings, configPendingIntent)

            val prefs = WidgetPrefs(context, widgetId)
            if (prefs.currentCategory < 0 || prefs.currentCategory > collections.size) {
                prefs.currentCategory = UnitCollection.DEFAULT_CATEGORY
            }
            if (prefs.currentFromIndex < 0 || prefs.currentFromIndex > collections[prefs.currentCategory].length()) {
                prefs.currentFromIndex = UnitCollection.DEFAULT_FROM_INDEX
            }
            if (prefs.currentToIndex < 0 || prefs.currentToIndex > collections[prefs.currentCategory].length()) {
                prefs.currentToIndex = UnitCollection.DEFAULT_TO_INDEX
            }
            remoteViews.setTextViewText(R.id.widget_unit_from,
                    Util.fromHtml(collections[prefs.currentCategory][prefs.currentFromIndex].name))
            remoteViews.setTextViewText(R.id.widget_unit_to,
                    Util.fromHtml(collections[prefs.currentCategory][prefs.currentToIndex].name))
            remoteViews.setTextViewText(R.id.widget_unit_from_value, ConvertActivity.getFormattedValueStr(prefs.currentValue.toDouble()))
            remoteViews.setTextViewText(R.id.widget_unit_to_value,
                    ConvertActivity.getFormattedValueStr(UnitCollection.convert(context, prefs.currentCategory, prefs.currentFromIndex, prefs.currentToIndex, prefs.currentValue.toDouble())))

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.isNullOrEmpty()) {
            return
        }
        when {
            intent.action!!.contains(CLICK_ACTION_PLUS) -> {
                val widgetId = getWidgetId(intent.action!!)
                val prefs = WidgetPrefs(context, widgetId)
                prefs.currentValue += prefs.increment
                prefs.save(context)
            }
            intent.action!!.contains(CLICK_ACTION_MINUS) -> {
                val widgetId = getWidgetId(intent.action!!)
                val prefs = WidgetPrefs(context, widgetId)
                prefs.currentValue -= prefs.increment
                prefs.save(context)
            }
            intent.action!!.contains(CLICK_ACTION_EXCHANGE) -> {
                val widgetId = getWidgetId(intent.action!!)
                val prefs = WidgetPrefs(context, widgetId)
                val fromIndex = prefs.currentFromIndex
                prefs.currentFromIndex = prefs.currentToIndex
                prefs.currentToIndex = fromIndex
                prefs.save(context)
            }
        }
    }

    private fun getSelfPendingIntent(context: Context, widgetId: Int, action: String): PendingIntent {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = action + "_" + widgetId
        return PendingIntent.getBroadcast(context, 0, intent, Util.getPendingIntentFlags())
    }

    companion object {
        private const val TAG = "WidgetProvider"
        const val CLICK_ACTION_SETTINGS = "actionSettings"
        private const val CLICK_ACTION_PLUS = "actionPlus"
        private const val CLICK_ACTION_MINUS = "actionMinus"
        private const val CLICK_ACTION_EXCHANGE = "actionExchange"
        fun getWidgetId(actionName: String): Int {
            return actionName.split("_")[1].toInt()
        }
    }
}