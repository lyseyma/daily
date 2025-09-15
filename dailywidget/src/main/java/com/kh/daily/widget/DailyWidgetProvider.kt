package com.kh.daily.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

/**
 * Legacy widget provider using traditional Android Views.
 * This is the older implementation before migrating to Jetpack Glance.
 *
 * Consider using TaskWidgetReceiver for a modern Compose-based widget experience.
 */
class DailyWidgetProvider : AppWidgetProvider() {

    /**
     * Called when the widget needs to be updated.
     * This method updates the widget UI with basic text content.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widgetTitle, "Today's Tasks ✅")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}