package com.defianttech.convertme;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class WidgetPrefs  {
    private final int widgetId;

    public int currentCategory;
    public int currentFromIndex;
    public int currentToIndex;
    public float currentValue;
    public float increment;

    public WidgetPrefs(@NonNull Context context, int widgetId) {
        this.widgetId = widgetId;
        SharedPreferences prefs = ConvertActivity.getPrefs(context);
        currentCategory = prefs.getInt("widget_category_" + widgetId, UnitCollection.DEFAULT_CATEGORY);
        currentFromIndex = prefs.getInt("widget_from_" + widgetId, UnitCollection.DEFAULT_FROM_INDEX);
        currentToIndex = prefs.getInt("widget_to_" + widgetId, UnitCollection.DEFAULT_TO_INDEX);
        increment = prefs.getFloat("widget_increment_" + widgetId, 1f);
        currentValue = prefs.getFloat("widget_from_value_" + widgetId, 1f);
    }

    public void save(@NonNull Context context) {
        SharedPreferences.Editor editor = ConvertActivity.getPrefs(context).edit();
        editor.putInt("widget_category_" + widgetId, currentCategory);
        editor.putInt("widget_from_" + widgetId, currentFromIndex);
        editor.putInt("widget_to_" + widgetId, currentToIndex);
        editor.putFloat("widget_increment_" + widgetId, increment);
        editor.putFloat("widget_from_value_" + widgetId, currentValue);
        editor.apply();

        Intent updateIntent = new Intent(context, WidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = { widgetId };
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(updateIntent);
    }
}
