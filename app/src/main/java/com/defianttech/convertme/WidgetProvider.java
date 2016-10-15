package com.defianttech.convertme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "WidgetProvider";

    public static final String CLICK_ACTION_SETTINGS = "actionSettings";
    private static final String CLICK_ACTION_PLUS = "actionPlus";
    private static final String CLICK_ACTION_MINUS = "actionMinus";
    private static final String CLICK_ACTION_EXCHANGE = "actionExchange";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        UnitCollection[] collections = UnitCollection.getInstance(context);

        for (int widgetId : allWidgetIds) {
            Log.d(TAG, "Updating widget ID " + widgetId);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_convert_units);

            remoteViews.setOnClickPendingIntent(R.id.widget_unit_plus, getSelfPendingIntent(context, widgetId, CLICK_ACTION_PLUS));
            remoteViews.setOnClickPendingIntent(R.id.widget_unit_minus, getSelfPendingIntent(context, widgetId, CLICK_ACTION_MINUS));
            remoteViews.setOnClickPendingIntent(R.id.widget_unit_container, getSelfPendingIntent(context, widgetId, CLICK_ACTION_EXCHANGE));

            Intent configIntent = new Intent(context, WidgetSetupActivity.class);
            configIntent.setAction(CLICK_ACTION_SETTINGS + "_" + widgetId);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_settings, configPendingIntent);

            WidgetPrefs prefs = new WidgetPrefs(context, widgetId);

            if (prefs.currentCategory < 0 || prefs.currentCategory > collections.length) {
                prefs.currentCategory = UnitCollection.DEFAULT_CATEGORY;
            }
            if (prefs.currentFromIndex < 0 || prefs.currentFromIndex > collections[prefs.currentCategory].length()) {
                prefs.currentFromIndex = UnitCollection.DEFAULT_FROM_INDEX;
            }
            if (prefs.currentToIndex < 0 || prefs.currentToIndex > collections[prefs.currentCategory].length()) {
                prefs.currentToIndex = UnitCollection.DEFAULT_TO_INDEX;
            }

            remoteViews.setTextViewText(R.id.widget_unit_from,
                    Html.fromHtml(collections[prefs.currentCategory].get(prefs.currentFromIndex).getName()));
            remoteViews.setTextViewText(R.id.widget_unit_to,
                    Html.fromHtml(collections[prefs.currentCategory].get(prefs.currentToIndex).getName()));
            remoteViews.setTextViewText(R.id.widget_unit_from_value, ConvertActivity.getFormattedValueStr(prefs.currentValue));
            remoteViews.setTextViewText(R.id.widget_unit_to_value,
                    ConvertActivity.getFormattedValueStr(UnitCollection.convert(context, prefs.currentCategory, prefs.currentFromIndex, prefs.currentToIndex, prefs.currentValue)));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (intent.getAction().contains(CLICK_ACTION_PLUS)) {
            int widgetId = getWidgetId(intent.getAction());
            WidgetPrefs prefs = new WidgetPrefs(context, widgetId);
            prefs.currentValue += 1f;
            prefs.save(context);
        } else if (intent.getAction().contains(CLICK_ACTION_MINUS)) {
            int widgetId = getWidgetId(intent.getAction());
            WidgetPrefs prefs = new WidgetPrefs(context, widgetId);
            prefs.currentValue -= 1f;
            prefs.save(context);
        } else if (intent.getAction().contains(CLICK_ACTION_EXCHANGE)) {
            int widgetId = getWidgetId(intent.getAction());
            WidgetPrefs prefs = new WidgetPrefs(context, widgetId);
            int fromIndex = prefs.currentFromIndex;
            prefs.currentFromIndex = prefs.currentToIndex;
            prefs.currentToIndex = fromIndex;
            prefs.save(context);
        }
    }

    private PendingIntent getSelfPendingIntent(Context context, int widgetId, String action) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(action + "_" + widgetId);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static int getWidgetId(@NonNull String actionName) {
        return Integer.parseInt(actionName.split("_")[1]);
    }
}
