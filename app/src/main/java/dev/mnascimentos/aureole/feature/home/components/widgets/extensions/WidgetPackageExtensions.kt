package dev.mnascimentos.aureole.feature.home.components.widgets.extensions

import android.appwidget.AppWidgetManager
import android.content.Context

internal fun resolveWidgetPackage(context: Context, widgetId: Int?): String? {
    if (widgetId == null || widgetId == -1) return null
    return try {
        AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetId)?.provider?.packageName
    } catch (_: Throwable) {
        null
    }
}
