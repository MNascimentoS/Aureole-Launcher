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
import android.widget.TextView
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.widget.model.PagerContentParams
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetItemActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_WIDGETS = 3
private const val LONG_PRESS_DURATION_MS = 1000L
private const val HAZE_MIN_ALPHA_ADD_BUTTON = 0.2f
private const val HAZE_MAX_ALPHA_ADD_BUTTON = 0.95f
private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val ADD_BUTTON_ALPHA = 0.4f
private const val ADD_BUTTON_HAZE_ALPHA_FACTOR = 0.7f
private const val WIDGET_MIN_WIDTH = 100
private const val WIDGET_MAX_HEIGHT = 360
private const val WIDGET_NOT_AVAILABLE_TEXT = "Widget não disponível (Remova e adicione novamente)"

@Composable
fun StackedWidgetSection(
    config: StackedWidgetConfig,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val showAddButton = config.topWidgetIds.size < MAX_WIDGETS
    val pageCount = config.topWidgetIds.size + if (showAddButton) 1 else 0
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val hasFillMaxSize = modifier.toString().contains("fillMaxSize") || modifier == Modifier.fillMaxSize()
    val (startPad, endPad) = calculateWidgetSectionPadding(
        hasFillMaxSize = hasFillMaxSize,
        isSidePanelEnabled = uiState.isSidePanelEnabled,
        isLeftHandedMode = uiState.isLeftHandedMode
    )

    Column(
        modifier = modifier
            .then(
                if (hasFillMaxSize) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.fillMaxWidth().padding(start = startPad, end = endPad, top = 4.dp, bottom = 4.dp)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StackedWidgetPagerContent(
            config = config,
            appWidgetHost = appWidgetHost,
            params = PagerContentParams(
                showAddButton = showAddButton,
                pageCount = pageCount,
                pagerState = pagerState,
                hasFillMaxSize = hasFillMaxSize
            )
        )

        if (pageCount > 1 && config.showWidgetDots) {
            PagerIndicatorDots(pageCount = pageCount, currentPage = pagerState.currentPage)
        }
    }
}

private fun calculateWidgetSectionPadding(
    hasFillMaxSize: Boolean,
    isSidePanelEnabled: Boolean,
    isLeftHandedMode: Boolean
): Pair<Dp, Dp> {
    val startPad = if (hasFillMaxSize) {
        0.dp
    } else if (isSidePanelEnabled) {
        if (isLeftHandedMode) 8.dp else 16.dp
    } else {
        16.dp
    }
    val endPad = if (hasFillMaxSize) {
        0.dp
    } else if (isSidePanelEnabled) {
        if (isLeftHandedMode) 16.dp else 8.dp
    } else {
        16.dp
    }
    return Pair(startPad, endPad)
}

@Composable
private fun StackedWidgetPagerContent(
    config: StackedWidgetConfig,
    appWidgetHost: AppWidgetHost,
    params: PagerContentParams
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val pageCount = params.pageCount
    val showAddButton = params.showAddButton
    val hasFillMaxSize = params.hasFillMaxSize

    if (pageCount > 0) {
        val pagerModifier = if (hasFillMaxSize) {
            Modifier.fillMaxSize()
        } else {
            Modifier.fillMaxWidth().height(config.currentHeightDp)
        }
        HorizontalPager(
            state = params.pagerState,
            modifier = pagerModifier
        ) { page ->
            if (page < config.topWidgetIds.size) {
                val widgetId = config.topWidgetIds[page]
                WidgetHostItem(
                    widgetId = widgetId,
                    appWidgetHost = appWidgetHost,
                    itemActions = WidgetItemActions(
                        onOpenWidgetPopup = { id, topY -> actions.onOpenWidgetPopup(id, topY) },
                        onRemoveClick = { id -> actions.onRemoveWidgetClick(id) }
                    ),
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
private fun PagerIndicatorDots(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = DEFAULT_HAZE_OPACITY)
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(6.dp)
            )
        }
    }
}

@Composable
private fun WidgetHostItem(
    widgetId: Int,
    appWidgetHost: AppWidgetHost,
    itemActions: WidgetItemActions,
    modifier: Modifier = Modifier
) {
    var itemYInWindow by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .onGloballyPositioned { coordinates ->
                itemYInWindow = coordinates.positionInWindow().y
            }
            .pointerInput(widgetId) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Main)
                    var isLongPressTriggered = false

                    val job = coroutineScope.launch {
                        delay(LONG_PRESS_DURATION_MS)
                        isLongPressTriggered = true
                        itemActions.onOpenWidgetPopup(widgetId, itemYInWindow)
                    }

                    try {
                        waitForUpOrCancellation(pass = PointerEventPass.Main)
                        job.cancel()
                        if (isLongPressTriggered) {
                            down.consume()
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
            update = { view ->
                val widthDp = view.context.resources.configuration.screenWidthDp
                if (view is AppWidgetHostView && view.tag != widthDp) {
                    view.tag = widthDp
                    val appWidgetManager = AppWidgetManager.getInstance(view.context)
                    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                    if (appWidgetInfo != null) {
                        val options = Bundle().apply {
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp.coerceAtLeast(WIDGET_MIN_WIDTH))
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, WIDGET_MAX_HEIGHT)
                        }
                        appWidgetManager.updateAppWidgetOptions(widgetId, options)
                    }
                }
            },
            modifier = Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection())
        )
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
            val widthDp = (context.resources.configuration.screenWidthDp).coerceAtLeast(WIDGET_MIN_WIDTH)
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, WIDGET_MAX_HEIGHT)
            }
            appWidgetManager.updateAppWidgetOptions(widgetId, options)
            appWidgetHost.createView(context, widgetId, appWidgetInfo)
        } catch (e: IllegalArgumentException) {
            Log.e("WidgetHostItem", "Error creating widget view", e)
            createErrorWidgetView(context, "Toque para reconfigurar widget")
        } catch (e: IllegalStateException) {
            Log.e("WidgetHostItem", "Error creating widget view", e)
            createErrorWidgetView(context, "Toque para reconfigurar widget")
        } catch (e: SecurityException) {
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
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Widget",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Add Widget",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
