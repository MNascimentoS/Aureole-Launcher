@file:Suppress("MagicNumber", "LongParameterList", "LongMethod", "UnusedParameter")

package dev.mnascimentos.aureole.ui.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_WIDGETS = 3

data class StackedWidgetSectionState(
    val topWidgetIds: List<Int>,
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val showWidgetDots: Boolean = true,
    val isLeftHandedMode: Boolean = false,
    val isSidePanelEnabled: Boolean = true,
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f
)

data class WidgetItemActions(
    val onOpenWidgetPopup: (Int, Float) -> Unit,
    val onRemoveClick: (Int) -> Unit
)

@Composable
fun StackedWidgetSection(
    topWidgetIds: List<Int>,
    appWidgetHost: AppWidgetHost,
    currentHeightDp: Dp,
    onHeightChange: (Float) -> Unit,
    actions: HomeScreenActions,
    currentHeightPx: Float,
    modifier: Modifier = Modifier,
    showWidgetDots: Boolean = true,
    isLeftHandedMode: Boolean = false,
    isSidePanelEnabled: Boolean = true,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = 0.5f
) {
    StackedWidgetSection(
        sectionState = StackedWidgetSectionState(
            topWidgetIds = topWidgetIds,
            currentHeightDp = currentHeightDp,
            currentHeightPx = currentHeightPx,
            showWidgetDots = showWidgetDots,
            isLeftHandedMode = isLeftHandedMode,
            isSidePanelEnabled = isSidePanelEnabled,
            hazeState = hazeState,
            isHazeEnabled = isHazeEnabled,
            hazeOpacity = hazeOpacity
        ),
        appWidgetHost = appWidgetHost,
        onHeightChange = onHeightChange,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun StackedWidgetSection(
    sectionState: StackedWidgetSectionState,
    appWidgetHost: AppWidgetHost,
    onHeightChange: (Float) -> Unit,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier
) {
    val showAddButton = sectionState.topWidgetIds.size < MAX_WIDGETS
    val pageCount = sectionState.topWidgetIds.size + if (showAddButton) 1 else 0
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val startPad = if (sectionState.isSidePanelEnabled) {
        if (sectionState.isLeftHandedMode) 8.dp else 16.dp
    } else {
        16.dp
    }
    val endPad = if (sectionState.isSidePanelEnabled) {
        if (sectionState.isLeftHandedMode) 16.dp else 8.dp
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
                    .height(sectionState.currentHeightDp)
            ) { page ->
                if (page < sectionState.topWidgetIds.size) {
                    val widgetId = sectionState.topWidgetIds[page]
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
                        hazeState = sectionState.hazeState,
                        isHazeEnabled = sectionState.isHazeEnabled,
                        hazeOpacity = sectionState.hazeOpacity
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(sectionState.currentHeightDp))
        }

        if (pageCount > 1 && sectionState.showWidgetDots) {
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
                        delay(1000L) // 1 second long press duration
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
    hazeOpacity: Float = 0.5f
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

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .then(hazeModifier)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = if (isHazeEnabled) (hazeOpacity * 0.7f).coerceIn(0.2f, 0.95f) else 0.4f
                )
            )
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
    widgetId: Int,
    appWidgetHost: AppWidgetHost,
    onDismiss: () -> Unit,
    onResizeClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = 0.5f
) {
    val hazeModifier = if (isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = isHazeEnabled
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .widthIn(min = 210.dp, max = 250.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(hazeModifier)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                    alpha = if (isHazeEnabled) (hazeOpacity * 0.8f).coerceIn(0.25f, 0.95f) else 1f
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Spacer(modifier = Modifier.height(12.dp))

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
