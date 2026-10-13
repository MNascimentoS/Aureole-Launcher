package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import kotlinx.coroutines.delay
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWidgetGroupScreen(
    item: LauncherItemState,
    widgets: List<Int>,
    appWidgetHost: AppWidgetHost? = null,
    isIndicatorEnabled: Boolean,
    onToggleIndicator: () -> Unit,
    onRemoveWidget: (Int) -> Unit,
    onAddWidgetClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onDeleteConfirm: (String) -> Unit,
    isBlurEnabled: Boolean = true,
    hazeState: HazeState? = null
) {
    var isVisible by remember { mutableStateOf(true) }
    var currentWidgets by remember(widgets) { mutableStateOf(widgets) }
    val listState = rememberLazyListState()
    val currentPage by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    BackHandler(enabled = isVisible) {
        isVisible = false
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(300)
        )
    ) {
        val hazeModifier = if (isBlurEnabled && hazeState != null) {
            Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 32.dp,
                    tint = HazeTint(Color.Black.copy(alpha = 0.55f))
                )
            ) {
                blurEnabled = true
            }
        } else {
            Modifier
        }

        val backgroundColor = if (isBlurEnabled && hazeState != null) {
            Color.Transparent
        } else {
            Color.Black
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(hazeModifier)
                .background(backgroundColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        isVisible = false
                    }
                )
        ) {
            var bottomSheetHeight by remember { mutableStateOf(0) }
            val density = LocalDensity.current

            // Bottom Sheet Surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { coordinates ->
                        bottomSheetHeight = coordinates.size.height
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume clicks on bottom sheet
                    ),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Widget Stack",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth(),
                        color = AureoleTheme.colors.onSurfaceHigh,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
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
                            checked = isIndicatorEnabled,
                            onCheckedChange = { onToggleIndicator() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isVisible = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar")
                    }
                }
            }

            val bottomSheetHeightDp = if (bottomSheetHeight == 0) 280.dp else with(density) { bottomSheetHeight.toDp() }

            // Preview Carousel positioned dynamically right above bottom sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomSheetHeightDp + 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        items(currentWidgets, key = { it }) { widgetId ->
                            WidgetPreviewItem(
                                widgetId = widgetId,
                                appWidgetHost = appWidgetHost,
                                onRemoveClick = {
                                    currentWidgets = currentWidgets.filter { id -> id != widgetId }
                                    onRemoveWidget(widgetId)
                                }
                            )
                        }
                        item {
                            AddWidgetCard(onClick = {
                                onAddWidgetClick()
                            })
                        }
                        item {
                            Spacer(modifier = Modifier.width(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dots indicator matching Home screen
                    if (currentWidgets.isNotEmpty() && isIndicatorEnabled) {
                        WidgetPositionIndicatorBar(
                            pageCount = currentWidgets.size,
                            currentPage = currentPage.coerceIn(0, (currentWidgets.size - 1).coerceAtLeast(0))
                        )
                    }
                }
            }

            if (!isVisible) {
                LaunchedEffect(isVisible) {
                    delay(300)
                    onDismissRequest()
                }
            }
        }
    }
}

@Composable
private fun WidgetPreviewItem(
    widgetId: Int,
    appWidgetHost: AppWidgetHost?,
    onRemoveClick: () -> Unit
) {
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
