package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.reorder.ReorderableRow
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.composable.widgets.WidgetPositionIndicatorBar
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPickerMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetPreviewPage

private const val PREVIEW_ITEM_COUNT = 3
private const val CARD_BORDER_ALPHA = 0.25f
private const val CARD_FILL_ALPHA = 0.6f
private const val ADD_CARD_BORDER_ALPHA = 0.2f
private const val ADD_CARD_FILL_ALPHA = 0.35f

/**
 * Live preview of the widget stack: hold a card and drag it sideways to reorder, ✕ removes it and the "+" card at
 * the end adds a widget. The position bar below follows the scroll.
 */
@Composable
fun EditWidgetGroupPreviewCarousel(
    params: EditWidgetCarouselParams,
    modifier: Modifier = Modifier
) {
    val widgets = params.currentWidgets
    val spacing = AureoleDS.dimens.small
    val slotPx = with(LocalDensity.current) { (WidgetPickerMetrics.PreviewCardWidth + spacing).toPx() }
    val currentPage = widgetPreviewPage(params.scrollState.value, slotPx, widgets.size)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReorderableRow(
            params = ReorderableParams(
                items = widgets,
                key = { it },
                actions = ReorderableActions(
                    onMove = params.onMoveWidget,
                    onMoveEnd = params.onMoveEnd,
                    onRemove = { index -> widgets.getOrNull(index)?.let(params.onRemoveWidget) }
                ),
                spacing = spacing,
                contentPadding = PaddingValues(horizontal = AureoleDS.dimens.medium)
            ),
            modifier = Modifier.fillMaxWidth(),
            scrollState = params.scrollState,
            trailingContent = { AddWidgetCard(onClick = params.onAddWidgetClick) }
        ) { widgetId ->
            WidgetPreviewCard(
                widgetId = widgetId,
                appWidgetHost = params.appWidgetHost,
                isCurrent = widgets.indexOf(widgetId) == currentPage
            )
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        if (widgets.isNotEmpty() && params.isIndicatorEnabled) {
            WidgetPositionIndicatorBar(
                pageCount = widgets.size,
                currentPage = currentPage
            )
        }
    }
}

/** One widget of the stack; the card of the page the position bar shows gets a clear border, as on the home. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WidgetPreviewCard(widgetId: Int, appWidgetHost: AppWidgetHost?, isCurrent: Boolean) {
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
    val border = if (isCurrent) {
        BorderStroke(AureoleDS.dimens.borderMax, AureoleDS.colors.onSurfaceHigh)
    } else {
        BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceMedium.copy(alpha = CARD_BORDER_ALPHA))
    }
    Box(
        modifier = sharedModifier.previewCardShape(
            border = border,
            fill = AureoleDS.colors.surfaceVariant.copy(alpha = CARD_FILL_ALPHA),
            shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
        ),
        contentAlignment = Alignment.Center
    ) {
        if (appWidgetHost != null) {
            AndroidView(
                factory = { ctx -> createWidgetHostView(ctx, appWidgetHost, widgetId) },
                modifier = Modifier.fillMaxSize()
            )
            TouchShield()
        } else {
            Text(
                text = stringResource(R.string.home_widgets_default_title),
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceHigh
            )
        }
    }
}

/**
 * Keeps touches away from the live widget below it, so a long press always starts the reorder drag instead of
 * being taken by the widget's own views. It consumes nothing: the card's drag gesture still gets every event.
 */
@Composable
private fun TouchShield() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) awaitPointerEvent(PointerEventPass.Initial)
                }
            }
    )
}

@Composable
private fun AddWidgetCard(onClick: () -> Unit) {
    val label = stringResource(R.string.home_widget_add)
    val border = BorderStroke(
        AureoleDS.dimens.borderMinimal,
        AureoleDS.colors.outline.copy(alpha = ADD_CARD_BORDER_ALPHA)
    )
    Box(
        modifier = Modifier
            .previewCardShape(
                border = border,
                fill = AureoleDS.colors.surfaceVariant.copy(alpha = ADD_CARD_FILL_ALPHA),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
            )
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = label
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Add(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

/** Size, rounded shape, border and fill shared by the widget cards and the "+" card. */
private fun Modifier.previewCardShape(border: BorderStroke, fill: Color, shape: Shape): Modifier = this
    .width(WidgetPickerMetrics.PreviewCardWidth)
    .height(WidgetPickerMetrics.PreviewCardHeight)
    .clip(shape)
    .background(fill)
    .border(border, shape)

@AureolePreview
@Composable
private fun EditWidgetGroupPreviewCarouselPreview() {
    var widgets by remember { mutableStateOf(List(PREVIEW_ITEM_COUNT) { it + 1 }) }
    AureoleLauncherTheme {
        EditWidgetGroupPreviewCarousel(
            params = EditWidgetCarouselParams(
                currentWidgets = widgets,
                appWidgetHost = null,
                scrollState = rememberScrollState(),
                isIndicatorEnabled = true,
                onRemoveWidget = { id -> widgets = widgets - id },
                onAddWidgetClick = {},
                onMoveWidget = { from, to -> widgets = move(widgets, from, to) }
            )
        )
    }
}
