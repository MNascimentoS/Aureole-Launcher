package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.widget.model.PagerContentParams
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COLOR_ANIMATION_DURATION_MS = 150
private const val LONG_PRESS_MARGIN_MS = 50L
private const val MIN_LONG_PRESS_TIMEOUT_MS = 200L

private const val MAX_WIDGETS = 5
private const val HAZE_MIN_ALPHA_ADD_BUTTON = 0.2f
private const val HAZE_MAX_ALPHA_ADD_BUTTON = 0.95f
private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val ADD_BUTTON_ALPHA = 0.4f
private const val ADD_BUTTON_HAZE_ALPHA_FACTOR = 0.7f
private const val WIDGET_NOT_AVAILABLE_TEXT = "Widget não disponível (Remova e adicione novamente)"

@Composable
fun StackedWidgetSection(
    config: StackedWidgetConfig,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier,
    stackId: String = "top_widgets"
) {
    val uiState = LocalHomeUiState.current
    val showAddButton = config.topWidgetIds.size < MAX_WIDGETS
    val pageCount = config.topWidgetIds.size + if (showAddButton) 1 else 0
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val showDotsForStack = uiState.widgetStackDots[stackId] ?: config.showWidgetDots

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StackedWidgetPagerContent(
            config = config,
            appWidgetHost = appWidgetHost,
            stackId = stackId,
            params = PagerContentParams(
                showAddButton = showAddButton,
                pageCount = pageCount,
                pagerState = pagerState,
                hasFillMaxSize = true
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        if (pageCount >= 1 && showDotsForStack) {
            WidgetPositionIndicatorBar(
                pageCount = pageCount,
                currentPage = pagerState.currentPage
            )
        }
    }
}

@Composable
private fun StackedWidgetPagerContent(
    config: StackedWidgetConfig,
    appWidgetHost: AppWidgetHost,
    stackId: String,
    params: PagerContentParams,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val pageCount = params.pageCount
    val showAddButton = params.showAddButton

    if (pageCount > 0) {
        HorizontalPager(
            state = params.pagerState,
            modifier = modifier
        ) { page ->
            if (page < config.topWidgetIds.size) {
                val widgetId = config.topWidgetIds[page]
                WidgetHostItem(
                    widgetId = widgetId,
                    stackId = stackId,
                    appWidgetHost = appWidgetHost,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (showAddButton) {
                AddWidgetButton(
                    onClick = actions.onAddWidgetClick,
                    modifier = Modifier.fillMaxSize(),
                    hazeState = config.hazeState,
                    isHazeEnabled = uiState.isHazeEnabled,
                    hazeOpacity = uiState.hazeOpacity
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(config.currentHeightDp))
    }
}

@Composable
fun WidgetPositionIndicatorBar(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 4.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = (index == currentPage)
            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) 22.dp else 8.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "indicator_width"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                },
                animationSpec = tween(COLOR_ANIMATION_DURATION_MS),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(5.dp)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

@Composable
private fun WidgetHostItem(
    widgetId: Int,
    stackId: String,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .pointerInput(widgetId, stackId) {
                val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    var isLongPressTriggered = false

                    val job = coroutineScope.launch {
                        delay((longPressTimeout - LONG_PRESS_MARGIN_MS).coerceAtLeast(MIN_LONG_PRESS_TIMEOUT_MS))
                        isLongPressTriggered = true
                        down.consume()
                        actions.onOpenWidgetStackBottomSheet(widgetId, stackId)
                    }

                    try {
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        job.cancel()
                        if (isLongPressTriggered) {
                            up?.consume()
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("WidgetHostItem", "Gesture error", e)
                        job.cancel()
                    }
                }
            }
    ) {
        AndroidView(
            factory = { context -> createWidgetHostView(context, appWidgetHost, widgetId) },
            update = { view -> updateWidgetHostView(view, widgetId) },
            modifier = Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection())
        )
    }
}

private fun updateWidgetHostView(view: View, widgetId: Int) {
    if (view is AppWidgetHostView) {
        val appWidgetManager = AppWidgetManager.getInstance(view.context)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        if (appWidgetInfo != null) {
            val density = view.context.resources.displayMetrics.density
            val measuredWidthDp = if (view.width > 0) {
                (view.width / density).toInt()
            } else {
                (view.context.resources.configuration.screenWidthDp)
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
    }
}

private fun createWidgetHostView(
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

private fun createErrorWidgetView(context: Context, message: String): TextView {
    return TextView(context).apply {
        text = message
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }
}

@Composable
private fun AddWidgetButton(
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
                tint = HazeTint(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = hazeOpacity))
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
            .clip(MaterialTheme.shapes.large)
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = buttonAlpha))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Add(
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Adicionar Widget",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
