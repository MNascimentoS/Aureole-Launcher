package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.bottomsheet.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import kotlinx.coroutines.delay
import android.graphics.Color as AndroidColor

private const val ANIMATION_DURATION_MS = 300

data class EditWidgetGroupParams(
    val item: LauncherItemState,
    val widgets: List<Int>,
    val appWidgetHost: AppWidgetHost? = null,
    val isIndicatorEnabled: Boolean = true,
    val hazeState: HazeState? = null
)

data class EditWidgetGroupActions(
    val onToggleIndicator: () -> Unit,
    val onRemoveWidget: (Int) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onDismissRequest: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWidgetGroupScreen(
    params: EditWidgetGroupParams,
    actions: EditWidgetGroupActions
) {
    val widgets = params.widgets
    var isVisible by remember { mutableStateOf(true) }
    var currentWidgets by remember(widgets) { mutableStateOf(widgets) }

    val listState = rememberLazyListState()
    val currentPage by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(ANIMATION_DURATION_MS.toLong())
            actions.onDismissRequest()
        }
    }

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        title = "Widget Stack",
        hazeState = params.hazeState,
        isVisible = isVisible,
        animated = true,
        previewContent = {
            EditWidgetGroupPreviewCarousel(
                currentWidgets = currentWidgets,
                appWidgetHost = params.appWidgetHost,
                listState = listState,
                currentPage = currentPage,
                isIndicatorEnabled = params.isIndicatorEnabled,
                onRemoveWidget = { widgetId ->
                    currentWidgets = currentWidgets.filter { id -> id != widgetId }
                    actions.onRemoveWidget(widgetId)
                },
                onAddWidgetClick = actions.onAddWidgetClick
            )
        },
        bottomBarContent = {
            Button(
                onClick = { isVisible = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "•••",
                    color = AureoleTheme.colors.onSurfaceMedium,
                    style = AureoleTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Show widget indicator", color = AureoleTheme.colors.onSurfaceMedium)
            }
            Switch(
                checked = params.isIndicatorEnabled,
                onCheckedChange = { actions.onToggleIndicator() }
            )
        }
    }
}

@Composable
private fun EditWidgetGroupPreviewCarousel(
    currentWidgets: List<Int>,
    appWidgetHost: AppWidgetHost?,
    listState: LazyListState,
    currentPage: Int,
    isIndicatorEnabled: Boolean,
    onRemoveWidget: (Int) -> Unit,
    onAddWidgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Spacer(modifier = Modifier.width(16.dp)) }
            items(currentWidgets, key = { it }) { widgetId ->
                WidgetPreviewItem(
                    widgetId = widgetId,
                    appWidgetHost = appWidgetHost,
                    onRemoveClick = { onRemoveWidget(widgetId) }
                )
            }
            item { AddWidgetCard(onClick = onAddWidgetClick) }
            item { Spacer(modifier = Modifier.width(28.dp)) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentWidgets.isNotEmpty() && isIndicatorEnabled) {
            WidgetPositionIndicatorBar(
                pageCount = currentWidgets.size,
                currentPage = currentPage.coerceIn(0, (currentWidgets.size - 1).coerceAtLeast(0))
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WidgetPreviewItem(
    widgetId: Int,
    appWidgetHost: AppWidgetHost?,
    onRemoveClick: () -> Unit
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "widget_$widgetId"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
    ) {
        // Outer border
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AureoleTheme.colors.onSurfaceMedium.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(6.dp)
        ) {
            // Inner card displaying widget
            Box(
                modifier = Modifier
                    .size(150.dp, 150.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .then(sharedModifier)
                    .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (appWidgetHost != null) {
                    AndroidView(
                        factory = { ctx ->
                            createWidgetHostView(ctx, appWidgetHost, widgetId)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Widget",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleTheme.colors.onSurfaceHigh
                    )
                }
            }
        }

        // "-" badge positioned at top-left
        Box(
            modifier = Modifier
                .offset(x = (-6).dp, y = (-6).dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(AureoleTheme.colors.surfaceVariant)
                .border(1.dp, AureoleTheme.colors.outline.copy(alpha = 0.3f), CircleShape)
                .clickable { onRemoveClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "–",
                color = AureoleTheme.colors.onSurfaceHigh,
                fontWeight = FontWeight.Bold,
                style = AureoleTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun AddWidgetCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(164.dp, 164.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.35f))
                .border(
                    width = 1.dp,
                    color = AureoleTheme.colors.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(28.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Add(
                tint = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.size(32.dp)
            )
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
            val hostView = appWidgetHost.createView(context, widgetId, appWidgetInfo)
            hostView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            hostView
        } catch (_: Exception) {
            TextView(context).apply {
                text = "Widget"
                gravity = Gravity.CENTER
                setTextColor(AndroidColor.WHITE)
            }
        }
    } else {
        TextView(context).apply {
            text = "Widget"
            gravity = Gravity.CENTER
            setTextColor(AndroidColor.WHITE)
        }
    }
}
