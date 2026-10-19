package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
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
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300

@Suppress("LongMethod") // Screen composable managing widget group editing layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWidgetGroupScreen(
    params: EditWidgetGroupParams,
    actions: EditWidgetGroupActions,
    modifier: Modifier = Modifier
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
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = "Widget Stack",
                hazeState = params.hazeState,
                isVisible = isVisible,
                animated = true
            ),
            previewContent = {
                EditWidgetGroupPreviewCarousel(
                    params = EditWidgetCarouselParams(
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
                )
            },
            bottomBarContent = {
                Button(
                    onClick = { isVisible = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar")
                }
            },
            sheetContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AureoleDS.dimens.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•••",
                            color = AureoleDS.colors.onSurfaceMedium,
                            style = AureoleDS.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
                        Text("Show widget indicator", color = AureoleDS.colors.onSurfaceMedium)
                    }
                    Switch(
                        checked = params.isIndicatorEnabled,
                        onCheckedChange = { actions.onToggleIndicator() }
                    )
                }
            }
        )
    )
}

@Composable
private fun EditWidgetGroupPreviewCarousel(
    params: EditWidgetCarouselParams,
    modifier: Modifier = Modifier
) {
    val currentWidgets = params.currentWidgets
    val appWidgetHost = params.appWidgetHost
    val listState = params.listState
    val currentPage = params.currentPage
    val isIndicatorEnabled = params.isIndicatorEnabled
    val onRemoveWidget = params.onRemoveWidget
    val onAddWidgetClick = params.onAddWidgetClick
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Spacer(modifier = Modifier.width(AureoleDS.dimens.medium)) }
            items(
                items = currentWidgets,
                key = { it },
                contentType = { "widget_preview" }
            ) { widgetId ->
                WidgetPreviewItem(
                    widgetId = widgetId,
                    appWidgetHost = appWidgetHost,
                    onRemoveClick = { onRemoveWidget(widgetId) }
                )
            }
            item { AddWidgetCard(onClick = onAddWidgetClick) }
            item { Spacer(modifier = Modifier.width(AureoleDS.dimens.large)) }
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        if (currentWidgets.isNotEmpty() && isIndicatorEnabled) {
            WidgetPositionIndicatorBar(
                pageCount = currentWidgets.size,
                currentPage = currentPage.coerceIn(0, (currentWidgets.size - 1).coerceAtLeast(0))
            )
        }
    }
}

@Suppress("LongMethod") // Preview item composable rendering shared element transitions
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
        modifier = Modifier.padding(top = AureoleDS.dimens.xSmall, start = AureoleDS.dimens.xSmall)
    ) {
        // Outer border
        Box(
            modifier = Modifier
                .border(
                    width = AureoleDS.dimens.borderMinimal,
                    color = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
                )
                .padding(AureoleDS.dimens.xxSmall)
        ) {
            // Inner card displaying widget
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.widgetPreviewCardSize)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
                    .then(sharedModifier)
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f)),
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
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleDS.colors.onSurfaceHigh
                    )
                }
            }
        }

        // "-" badge positioned at top-left
        Box(
            modifier = Modifier
                .offset(x = -AureoleDS.dimens.xxSmall, y = -AureoleDS.dimens.xxSmall)
                .size(AureoleDS.dimens.large)
                .clip(CircleShape)
                .background(AureoleDS.colors.surfaceVariant)
                .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = 0.3f), CircleShape)
                .clickable { onRemoveClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "–",
                color = AureoleDS.colors.onSurfaceHigh,
                fontWeight = FontWeight.Bold,
                style = AureoleDS.typography.titleMedium
            )
        }
    }
}

@Composable
private fun AddWidgetCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(top = AureoleDS.dimens.xSmall, start = AureoleDS.dimens.xSmall)
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.widgetAddCardSize)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
                .border(
                    width = AureoleDS.dimens.borderMinimal,
                    color = AureoleDS.colors.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.xLarge)
            )
        }
    }
}

@AureolePreview
@Composable
private fun EditWidgetGroupScreenPreview() {
    AureoleLauncherTheme {
        EditWidgetGroupScreen(
            params = EditWidgetGroupParams(
                item = LauncherItemState(id = "preview"),
                widgets = listOf(1, 2)
            ),
            actions = EditWidgetGroupActions(
                onToggleIndicator = {},
                onRemoveWidget = {},
                onAddWidgetClick = {},
                onDismissRequest = {}
            )
        )
    }
}
