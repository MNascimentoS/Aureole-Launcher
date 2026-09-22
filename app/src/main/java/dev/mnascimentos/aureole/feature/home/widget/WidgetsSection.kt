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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.model.OpenedWidgetPopupConfig
import dev.mnascimentos.aureole.feature.home.widget.model.PagerContentParams
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetItemActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_WIDGETS = 3
private const val LONG_PRESS_DURATION_MS = 1000L
private const val HAZE_MIN_ALPHA_ADD_BUTTON = 0.2f
private const val HAZE_MAX_ALPHA_ADD_BUTTON = 0.95f
private const val HAZE_MIN_ALPHA_POPUP = 0.25f
private const val HAZE_MAX_ALPHA_POPUP = 0.95f
private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val ADD_BUTTON_ALPHA = 0.4f
private const val ADD_BUTTON_HAZE_ALPHA_FACTOR = 0.7f
private const val POPUP_HAZE_ALPHA_FACTOR = 0.8f
private const val PREVIEW_APPWIDGET_HOST_ID = 1024
private const val PREVIEW_HEIGHT_PX = 400f
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
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp.coerceAtLeast(100))
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 360)
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
            val widthDp = (context.resources.configuration.screenWidthDp).coerceAtLeast(100)
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 360)
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

@Composable
fun OpenedWidgetPopup(
    config: OpenedWidgetPopupConfig,
    modifier: Modifier = Modifier
) {
    val hazeModifier = if (config.isHazeEnabled && (config.hazeState != null)) {
        Modifier.hazeEffect(
            state = config.hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = config.hazeOpacity))
            )
        ) {
            blurEnabled = config.isHazeEnabled
        }
    } else {
        Modifier
    }

    val popupAlpha = if (config.isHazeEnabled) {
        (config.hazeOpacity * POPUP_HAZE_ALPHA_FACTOR).coerceIn(HAZE_MIN_ALPHA_POPUP, HAZE_MAX_ALPHA_POPUP)
    } else {
        1f
    }

    Box(
        modifier = modifier
            .widthIn(min = 210.dp, max = 250.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = popupAlpha))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OpenedWidgetPopupHeader(onDismiss = config.onDismiss)
            Spacer(modifier = Modifier.height(12.dp))
            OpenedWidgetPopupActions(
                onResizeClick = config.onResizeClick,
                onRemoveClick = config.onRemoveClick
            )
        }
    }
}

@Composable
private fun OpenedWidgetPopupHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Widget Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun OpenedWidgetPopupActions(
    onResizeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Button(
        onClick = onResizeClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Resize Widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onRemoveClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Remove Widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun WidgetResizeDialog(
    currentHeightDp: Dp,
    onHeightSelected: (Dp) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        120.dp to "Compact (120 dp)",
        160.dp to "Standard (160 dp)",
        240.dp to "Large (240 dp)",
        360.dp to "Extra Large (360 dp)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Resize Widget Row",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                options.forEach { (height, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onHeightSelected(height) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentHeightDp == height),
                            onClick = { onHeightSelected(height) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@AureolePreview
@Composable
fun StackedWidgetSectionPreview() {
    val context = LocalContext.current
    val mockUiState = MainUiState()
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides mockActions
        ) {
            StackedWidgetSection(
                config = StackedWidgetConfig(
                    topWidgetIds = emptyList(),
                    currentHeightDp = 160.dp,
                    currentHeightPx = PREVIEW_HEIGHT_PX
                ),
                appWidgetHost = remember { AppWidgetHost(context, PREVIEW_APPWIDGET_HOST_ID) }
            )
        }
    }
}

@AureolePreview
@Composable
fun WidgetResizeDialogPreview() {
    AureoleLauncherTheme {
        WidgetResizeDialog(
            currentHeightDp = 160.dp,
            onHeightSelected = {},
            onDismiss = {}
        )
    }
}

@AureolePreview
@Composable
fun OpenedWidgetPopupPreview() {
    AureoleLauncherTheme {
        OpenedWidgetPopup(
            config = OpenedWidgetPopupConfig(
                onDismiss = {},
                onResizeClick = {},
                onRemoveClick = {}
            )
        )
    }
}
