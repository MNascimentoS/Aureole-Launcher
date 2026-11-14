package dev.mnascimentos.aureole.feature.home.components.content

import android.annotation.SuppressLint
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.home.components.widgets.rememberWidgetThemeColorsApplier

private const val DEFAULT_WIDGET_MIN_WIDTH = 100
private const val DEFAULT_WIDGET_MAX_HEIGHT = 360

@Composable
internal fun SingleWidgetHostView(widgetId: Int, appWidgetHost: AppWidgetHost?) {
    val applyThemeColors = rememberWidgetThemeColorsApplier()
    AndroidView(
        factory = { context -> createSingleWidgetView(context, widgetId, appWidgetHost) },
        update = { view ->
            updateSingleWidgetView(view, widgetId)
            applyThemeColors(view)
        },
        modifier = Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection())
    )
}

@SuppressLint("SetTextI18n")
private fun createSingleWidgetView(context: Context, widgetId: Int, appWidgetHost: AppWidgetHost?): View {
    if (appWidgetHost == null) {
        return TextView(
            context
        ).apply { text = context.getString(R.string.home_grid_widget_label) }
    }
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
    return if (appWidgetInfo != null) {
        val widthDp = (context.resources.configuration.screenWidthDp).coerceAtLeast(DEFAULT_WIDGET_MIN_WIDTH)
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, DEFAULT_WIDGET_MAX_HEIGHT)
        }
        appWidgetManager.updateAppWidgetOptions(widgetId, options)
        appWidgetHost.createView(context, widgetId, appWidgetInfo)
    } else {
        TextView(context).apply { text = context.getString(R.string.home_grid_widget_label) }
    }
}

private fun updateSingleWidgetView(view: View, widgetId: Int) {
    val widthDp = view.context.resources.configuration.screenWidthDp
    if (view is AppWidgetHostView && view.tag != widthDp) {
        view.tag = widthDp
        val appWidgetManager = AppWidgetManager.getInstance(view.context)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        if (appWidgetInfo != null) {
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp.coerceAtLeast(DEFAULT_WIDGET_MIN_WIDTH))
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, DEFAULT_WIDGET_MAX_HEIGHT)
            }
            appWidgetManager.updateAppWidgetOptions(widgetId, options)
        }
    }
}
