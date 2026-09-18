@file:Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught", "SwallowedException", "MaxLineLength", "MagicNumber", "FunctionNaming")

package dev.mnascimentos.aureole.ui.screens

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.components.AlphabetScrubber
import dev.mnascimentos.aureole.ui.components.AppItemRow
import dev.mnascimentos.aureole.ui.components.ClockHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private const val AUTO_HIDE_DELAY_MS = 5000L
private const val SWIPE_THRESHOLD = -10f
private const val LONG_PRESS_TIMEOUT_MS = 500L
private val WIDGET_WIDTH = 320.dp
private val ADD_WIDGET_WIDTH = 120.dp
private val MIN_WIDGET_HEIGHT = 100.dp
private val MAX_WIDGET_HEIGHT = 600.dp

data class HomeScreenActions(
    val onWidgetRowHeightChanged: (Dp) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onRemoveWidgetClick: (Int) -> Unit,
    val onAppClick: (AppInfo) -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    uiState: MainUiState,
    topWidgetIds: List<Int>,
    appWidgetHost: AppWidgetHost,
    widgetRowHeight: Dp,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var currentHeightPx by remember(widgetRowHeight) {
        mutableFloatStateOf(with(density) { widgetRowHeight.toPx() })
    }
    
    var showScrubber by remember { mutableStateOf(false) }
    var hideScrubberJob by remember { mutableStateOf<Job?>(null) }
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    LaunchedEffect(listState.isScrollInProgress, listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex < 2) {
            showScrubber = false
            hideScrubberJob?.cancel()
        } else if (listState.isScrollInProgress) {
            showScrubber = true
            hideScrubberJob?.cancel()
            hideScrubberJob = coroutineScope.launch {
                delay(AUTO_HIDE_DELAY_MS)
                showScrubber = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount.x < SWIPE_THRESHOLD) {
                            showScrubber = true
                            hideScrubberJob?.cancel()
                            hideScrubberJob = coroutineScope.launch {
                                delay(AUTO_HIDE_DELAY_MS)
                                showScrubber = false
                            }
                        }
                    }
                )
            }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxSize()
                ) {
                    item(key = "clock_header") { ClockHeader() }

                    item(key = "widgets_row") {
                        WidgetsSection(
                            topWidgetIds = topWidgetIds,
                            appWidgetHost = appWidgetHost,
                            currentHeightDp = currentHeightDp,
                            onHeightChange = { newHeightPx -> currentHeightPx = newHeightPx },
                            actions = actions,
                            currentHeightPx = currentHeightPx
                        )
                    }

                    item(key = "spacer") { Spacer(modifier = Modifier.height(8.dp)) }

                    appsListItems(uiState, actions)
                }
            }

            ScrubberOverlay(
                uiState = uiState,
                showScrubber = showScrubber,
                listState = listState,
                coroutineScope = coroutineScope,
                onInteraction = {
                    hideScrubberJob?.cancel()
                    hideScrubberJob = coroutineScope.launch {
                        delay(AUTO_HIDE_DELAY_MS)
                        showScrubber = false
                    }
                }
            )
        }
    }
}

@Composable
private fun WidgetsSection(
    topWidgetIds: List<Int>,
    appWidgetHost: AppWidgetHost,
    currentHeightDp: Dp,
    onHeightChange: (Float) -> Unit,
    actions: HomeScreenActions,
    currentHeightPx: Float
) {
    var showMenuForWidget by remember { mutableStateOf<Int?>(null) }
    var isResizing by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeightDp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(topWidgetIds, key = { it }) { widgetId ->
                WidgetHostItem(
                    widgetId = widgetId,
                    appWidgetHost = appWidgetHost,
                    showMenu = showMenuForWidget == widgetId,
                    onShowMenu = { showMenuForWidget = it },
                    onResizeClick = {
                        showMenuForWidget = null
                        isResizing = true
                    },
                    onRemoveClick = {
                        showMenuForWidget = null
                        actions.onRemoveWidgetClick(it)
                    }
                )
            }

            item {
                AddWidgetButton(onClick = actions.onAddWidgetClick)
            }
        }

        if (isResizing) {
            ResizeHandle(
                onDragDelta = { dragAmount -> 
                    val newHeight = (currentHeightPx + dragAmount).coerceIn(
                        with(density) { MIN_WIDGET_HEIGHT.toPx() },
                        with(density) { MAX_WIDGET_HEIGHT.toPx() }
                    )
                    onHeightChange(newHeight)
                },
                onDragEnd = {
                    isResizing = false
                    actions.onWidgetRowHeightChanged(currentHeightDp)
                }
            )
        }
    }
}

@Composable
private fun WidgetHostItem(
    widgetId: Int,
    appWidgetHost: AppWidgetHost,
    showMenu: Boolean,
    onShowMenu: (Int?) -> Unit,
    onResizeClick: () -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(WIDGET_WIDTH)
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
                            onShowMenu(widgetId)
                            withTimeoutOrNull(LONG_PRESS_TIMEOUT_MS * 4) {
                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            }
                        }
                    } catch (e: Exception) {
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
                onDismiss = { onShowMenu(null) },
                onResizeClick = onResizeClick,
                onRemoveClick = { onRemoveClick(widgetId) }
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
private fun AddWidgetButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(ADD_WIDGET_WIDTH)
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
                text = "Add",
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

private fun LazyListScope.appsListItems(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    itemsIndexed(
        items = uiState.filteredApps,
        key = { _, app -> app.packageName }
    ) { index, app ->
        val currentLetter = app.firstLetter
        
        val isFirstOfLetter = index == 0 || uiState.filteredApps[index - 1].firstLetter != currentLetter

        if (isFirstOfLetter) {
            Text(
                text = currentLetter.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 16.dp,
                    bottom = 4.dp
                )
            )
        }

        AppItemRow(
            app = app,
            onClick = { actions.onAppClick(app) }
        )
    }
}

@Composable
private fun BoxScope.ScrubberOverlay(
    uiState: MainUiState,
    showScrubber: Boolean,
    listState: LazyListState,
    onInteraction: () -> Unit,
    coroutineScope: CoroutineScope
) {
    AnimatedVisibility(
        visible = showScrubber,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        AlphabetScrubber(
            alphabet = uiState.alphabet,
            onLetterSelected = { letter ->
                uiState.letterIndexMap[letter]?.let { targetIndex ->
                    coroutineScope.launch {
                        listState.scrollToItem(targetIndex + 3)
                    }
                }
                onInteraction()
            }
        )
    }
}
