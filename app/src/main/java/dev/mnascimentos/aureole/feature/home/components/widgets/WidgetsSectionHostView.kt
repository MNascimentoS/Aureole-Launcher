package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import dev.mnascimentos.aureole.R
import timber.log.Timber

private const val WIDGET_NOT_AVAILABLE_TEXT = "Widget indisponível"

internal fun applyWidgetOptionsIfNeeded(
    view: AppWidgetHostView,
    widgetId: Int,
    appWidgetManager: AppWidgetManager,
    appWidgetInfo: AppWidgetProviderInfo
) {
    val density = view.context.resources.displayMetrics.density
    val measuredWidthDp = if (view.width > 0) {
        (view.width / density).toInt()
    } else {
        view.context.resources.configuration.screenWidthDp
    }
    val measuredHeightDp = if (view.height > 0) {
        (view.height / density).toInt()
    } else {
        appWidgetInfo.minHeight
    }
    val tagKey = "${measuredWidthDp}x$measuredHeightDp"
    if (view.tag != tagKey) {
        view.tag = tagKey
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
            putInt(
                AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                measuredWidthDp.coerceAtLeast(appWidgetInfo.minWidth)
            )
            putInt(
                AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                measuredHeightDp.coerceAtLeast(appWidgetInfo.minHeight)
            )
        }
        appWidgetManager.updateAppWidgetOptions(widgetId, options)
    }
}

internal fun createWidgetHostView(
    context: Context,
    appWidgetHost: AppWidgetHost?,
    widgetId: Int
): View {
    if (appWidgetHost == null) {
        return createErrorWidgetView(context, WIDGET_NOT_AVAILABLE_TEXT)
    }
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
    return if (appWidgetInfo != null) {
        try {
            val displayMetrics = context.resources.displayMetrics
            val widthDp = (displayMetrics.widthPixels / displayMetrics.density).toInt().coerceAtLeast(
                appWidgetInfo.minWidth
            )
            val heightDp = (displayMetrics.heightPixels / displayMetrics.density).toInt().coerceAtLeast(
                appWidgetInfo.minHeight
            )
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, heightDp)
            }
            appWidgetManager.updateAppWidgetOptions(widgetId, options)
            val hostView = appWidgetHost.createView(context, widgetId, appWidgetInfo)
            hostView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            hostView
        } catch (e: SecurityException) {
            Timber.tag("WidgetHostItem").e(e, "Error creating widget view")
            createErrorWidgetView(context, context.getString(R.string.home_widget_tap_to_configure))
        } catch (e: IllegalArgumentException) {
            Timber.tag("WidgetHostItem").e(e, "Error creating widget view")
            createErrorWidgetView(context, context.getString(R.string.home_widget_tap_to_configure))
        } catch (e: IllegalStateException) {
            Timber.tag("WidgetHostItem").e(e, "Error creating widget view")
            createErrorWidgetView(context, context.getString(R.string.home_widget_tap_to_configure))
        }
    } else {
        createErrorWidgetView(context, context.getString(R.string.home_widget_unavailable))
    }
}

internal fun createErrorWidgetView(context: Context, message: String): TextView {
    return TextView(context).apply {
        text = message
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }
}
