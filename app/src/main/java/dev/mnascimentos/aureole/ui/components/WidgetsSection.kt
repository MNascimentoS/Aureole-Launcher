package dev.mnascimentos.aureole.ui.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions

private const val LONG_PRESS_TIMEOUT_MS = 500L
private const val LONG_PRESS_CANCEL_MULTIPLIER = 4
private const val MAX_WIDGETS = 3
private val MIN_WIDGET_HEIGHT = 100.dp
private val MAX_WIDGET_HEIGHT = 600.dp

data class StackedWidgetSectionState(
    val topWidgetIds: List<Int>,
    val currentHeightDp: Dp,
    val currentHeightPx: Float
)

data class WidgetItemActions(
    val onShowMenu: (Int?) -> Unit,
    val onResizeClick: () -> Unit,
    val onRemoveClick: (Int) -> Unit
)

@Suppress("LongParameterList")
@Composable
fun StackedWidgetSection(
    topWidgetIds: List<Int>,
    appWidgetHost: AppWidgetHost,
    currentHeightDp: Dp,
    onHeightChange: (Float) -> Unit,
    actions: HomeScreenActions,
    currentHeightPx: Float,
    modifier: Modifier = Modifier
) {
    StackedWidgetSection(
        sectionState = StackedWidgetSectionState(
            topWidgetIds = topWidgetIds,
            currentHeightDp = currentHeightDp,
            currentHeightPx = currentHeightPx
        ),
        appWidgetHost = appWidgetHost,
        onHeightChange = onHeightChange,
        actions = actions,
        modifier = modifier
    )
}

@Suppress("LongMethod")
@Composable
fun StackedWidgetSection(
    sectionState: StackedWidgetSectionState,
    appWidgetHost: AppWidgetHost,
    onHeightChange: (Float) -> Unit,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier
) {
    var showMenuForWidget by remember { mutableStateOf<Int?>(null) }
    var isResizing by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val showAddButton = sectionState.topWidgetIds.size < MAX_WIDGETS
    val pageCount = sectionState.topWidgetIds.size + if (showAddButton) 1 else 0
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (pageCount > 0) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sectionState.currentHeightDp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { page ->
                if (page < sectionState.topWidgetIds.size) {
                    val widgetId = sectionState.topWidgetIds[page]
                    WidgetHostItem(
                        widgetId = widgetId,
                        appWidgetHost = appWidgetHost,
                        showMenu = showMenuForWidget == widgetId,
                        itemActions = WidgetItemActions(
                            onShowMenu = { showMenuForWidget = it },
                            onResizeClick = {
                                showMenuForWidget = null
                                isResizing = true
                            },
                            onRemoveClick = { id ->
                                showMenuForWidget = null
                                actions.onRemoveWidgetClick(id)
                            }
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (showAddButton) {
                    AddWidgetButton(
                        onClick = actions.onAddWidgetClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(sectionState.currentHeightDp))
        }

        if (isResizing) {
            ResizeHandle(
                onDragDelta = { dragAmount ->
                    val newHeight = (sectionState.currentHeightPx + dragAmount).coerceIn(
                        with(density) { MIN_WIDGET_HEIGHT.toPx() },
                        with(density) { MAX_WIDGET_HEIGHT.toPx() }
                    )
                    onHeightChange(newHeight)
                },
                onDragEnd = {
                    isResizing = false
                    actions.onWidgetRowHeightChanged(sectionState.currentHeightDp)
                }
            )
        } else if (pageCount > 1) {
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
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
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
    showMenu: Boolean,
    itemActions: WidgetItemActions,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .pointerInput(widgetId) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    try {
                        val upOrCancel = withTimeoutOrNull(LONG_PRESS_TIMEOUT_MS) {
                            waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        }
                        if (upOrCancel == null) {
                            down.consume()
                            itemActions.onShowMenu(widgetId)
                            withTimeoutOrNull(LONG_PRESS_TIMEOUT_MS * LONG_PRESS_CANCEL_MULTIPLIER) {
                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            }
                        }
                    } catch (_: Exception) {
                        // Avoid crashing gesture handler on unexpected cancellations
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

        if (showMenu) {
            WidgetOverlayMenu(
                onDismiss = { itemActions.onShowMenu(null) },
                onResizeClick = itemActions.onResizeClick,
                onRemoveClick = { itemActions.onRemoveClick(widgetId) }
            )
        }
    }
}

@Composable
private fun WidgetOverlayMenu(
    onDismiss: () -> Unit,
    onResizeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OverlayActionButton(
                icon = Icons.Default.Edit,
                text = "Resize",
                onClick = onResizeClick
            )
            OverlayActionButton(
                icon = Icons.Default.Delete,
                text = "Delete",
                onClick = onRemoveClick
            )
        }
    }
}

@Composable
private fun OverlayActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun AddWidgetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
private fun ResizeHandle(
    onDragDelta: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = onDragEnd
                ) { change, dragAmount ->
                    change.consume()
                    onDragDelta(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
