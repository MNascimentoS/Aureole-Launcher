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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.widgets.WidgetPositionIndicatorBar
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPickerMetrics
import kotlinx.coroutines.delay

private const val ROTATION_LEFT_DEGREES = -90f
private const val ROTATION_RIGHT_DEGREES = 90f
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

    val onMoveWidget = { fromIndex: Int, toIndex: Int ->
        if (fromIndex in currentWidgets.indices && toIndex in currentWidgets.indices) {
            val updated = currentWidgets.toMutableList().apply {
                val item = removeAt(fromIndex)
                add(toIndex, item)
            }
            currentWidgets = updated
            actions.onReorderWidgets(updated)
        }
    }

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
                title = stringResource(R.string.home_widget_stack_title),
                hazeState = params.hazeState,
                maxSheetHeightFraction = 0.3f
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
                        onAddWidgetClick = actions.onAddWidgetClick,
                        onMoveWidget = onMoveWidget
                    )
                )
            },
            bottomBarContent = {
                Button(
                    onClick = { isVisible = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.home_container_edit_save))
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
                        Text(
                            stringResource(R.string.home_widget_show_indicator),
                            color = AureoleDS.colors.onSurfaceMedium
                        )
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
    val onMoveWidget = params.onMoveWidget
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
            itemsIndexed(
                items = currentWidgets,
                key = { _, widgetId -> widgetId },
                contentType = { _, _ -> "widget_preview" }
            ) { index, widgetId ->
                WidgetPreviewItem(
                    widgetId = widgetId,
                    appWidgetHost = appWidgetHost,
                    onRemoveClick = { onRemoveWidget(widgetId) },
                    onMoveLeft = if (index > 0) {
                        { onMoveWidget?.invoke(index, index - 1) }
                    } else {
                        null
                    },
                    onMoveRight = if (index < currentWidgets.size - 1) {
                        { onMoveWidget?.invoke(index, index + 1) }
                    } else {
                        null
                    }
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
    onRemoveClick: () -> Unit,
    onMoveLeft: (() -> Unit)? = null,
    onMoveRight: (() -> Unit)? = null
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
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
                    .size(WidgetPickerMetrics.PreviewCardSize)
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
                        text = stringResource(R.string.home_widgets_default_title),
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleDS.colors.onSurfaceHigh
                    )
                }
            }
        }

        // Action buttons row matching Edit Shortcuts style
        Row(
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onMoveLeft != null) {
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.large)
                        .background(
                            color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                        )
                        .clickable { onMoveLeft() },
                    contentAlignment = Alignment.Center
                ) {
                    AureoleDS.icons.ArrowUp(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier
                            .size(AureoleDS.dimens.iconSmall)
                            .rotate(ROTATION_LEFT_DEGREES)
                    )
                }
            }

            if (onMoveRight != null) {
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.large)
                        .background(
                            color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                        )
                        .clickable { onMoveRight() },
                    contentAlignment = Alignment.Center
                ) {
                    AureoleDS.icons.ArrowUp(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier
                            .size(AureoleDS.dimens.iconSmall)
                            .rotate(ROTATION_RIGHT_DEGREES)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.large)
                    .background(
                        color = AureoleDS.colors.error,
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                    )
                    .clickable { onRemoveClick() },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Delete(
                    tint = AureoleDS.colors.onError,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
    }
}

@Composable
private fun AddWidgetCard(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = AureoleDS.dimens.borderMinimal,
                    color = AureoleDS.colors.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
                )
                .padding(AureoleDS.dimens.xxSmall)
        ) {
            Box(
                modifier = Modifier
                    .size(WidgetPickerMetrics.PreviewCardSize)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Add(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.xLarge)
                )
            }
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
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
