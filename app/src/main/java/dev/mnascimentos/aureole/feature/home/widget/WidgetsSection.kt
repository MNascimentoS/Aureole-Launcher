package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.res.Configuration
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.MainUiState
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

data class StackedWidgetConfig(
    val topWidgetIds: List<Int>,
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val showWidgetDots: Boolean = true,
    val hazeState: HazeState? = null
)

data class OpenedWidgetPopupConfig(
    val onDismiss: () -> Unit,
    val onResizeClick: () -> Unit,
    val onRemoveClick: () -> Unit,
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = DEFAULT_HAZE_OPACITY
)

data class WidgetItemActions(
    val onOpenWidgetPopup: (Int, Float) -> Unit,
    val onRemoveClick: (Int) -> Unit
)

@Composable
fun StackedWidgetSection(
    config: StackedWidgetConfig,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val showAddButton = config.topWidgetIds.size < MAX_WIDGETS
    val pageCount = config.topWidgetIds.size + if (showAddButton) 1 else 0
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val startPad = if (uiState.isSidePanelEnabled) {
        if (uiState.isLeftHandedMode) 8.dp else 16.dp
    } else {
        16.dp
    }
    val endPad = if (uiState.isSidePanelEnabled) {
        if (uiState.isLeftHandedMode) 16.dp else 8.dp
    } else {
        16.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startPad, end = endPad, top = 4.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pageCount > 0) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.currentHeightDp)
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

        if (pageCount > 1 && config.showWidgetDots) {
            PagerIndicatorDots(pageCount = pageCount, currentPage = pagerState.currentPage)
        }
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
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    var isLongPressTriggered = false

                    val job = coroutineScope.launch {
                        delay(LONG_PRESS_DURATION_MS)
                        isLongPressTriggered = true
                        itemActions.onOpenWidgetPopup(widgetId, itemYInWindow)
                    }

                    try {
                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        job.cancel()
                        if (isLongPressTriggered) {
                            down.consume()
                        }
                    } catch (_: Exception) {
                        job.cancel()
                    }
                }
            }
    ) {
        AndroidView(
            factory = { context ->
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                appWidgetHost.createView(context, widgetId, appWidgetInfo)
            },
            modifier = Modifier.fillMaxSize()
        )
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
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
