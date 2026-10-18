package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

private const val WIDGET_NOT_AVAILABLE_TEXT = "Widget indisponível"
private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val ADD_BUTTON_HAZE_ALPHA_FACTOR = 0.8f
private const val HAZE_MIN_ALPHA_ADD_BUTTON = 0.2f
private const val HAZE_MAX_ALPHA_ADD_BUTTON = 0.6f
private const val ADD_BUTTON_ALPHA = 0.5f

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
    appWidgetHost: AppWidgetHost,
    widgetId: Int
): View {
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
            Log.e("WidgetHostItem", "Error creating widget view", e)
            createErrorWidgetView(context, "Toque para reconfigurar widget")
        } catch (e: IllegalArgumentException) {
            Log.e("WidgetHostItem", "Error creating widget view", e)
            createErrorWidgetView(context, "Toque para reconfigurar widget")
        } catch (e: IllegalStateException) {
            Log.e("WidgetHostItem", "Error creating widget view", e)
            createErrorWidgetView(context, "Toque para reconfigurar widget")
        }
    } else {
        createErrorWidgetView(context, WIDGET_NOT_AVAILABLE_TEXT)
    }
}

internal fun createErrorWidgetView(context: Context, message: String): TextView {
    return TextView(context).apply {
        text = message
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }
}

@Composable
internal fun AddWidgetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = DEFAULT_HAZE_OPACITY
) {
    val hazeModifier = if (isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = isHazeEnabled
        }
    } else {
        Modifier
    }

    val buttonAlpha = if (isHazeEnabled) {
        (hazeOpacity * ADD_BUTTON_HAZE_ALPHA_FACTOR).coerceIn(
            HAZE_MIN_ALPHA_ADD_BUTTON,
            HAZE_MAX_ALPHA_ADD_BUTTON
        )
    } else {
        ADD_BUTTON_ALPHA
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(hazeModifier)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = buttonAlpha))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Adicionar Widget",
                color = AureoleDS.colors.onSurfaceHigh,
                style = AureoleDS.typography.labelLarge
            )
        }
    }
}
