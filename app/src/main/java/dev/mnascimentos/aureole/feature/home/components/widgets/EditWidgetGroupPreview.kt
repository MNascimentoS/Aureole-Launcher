package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
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
import dev.mnascimentos.aureole.feature.home.components.widgets.model.RevealScrollParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.revealScrollTarget
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetCardHeight
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetCardWidth
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetPreviewPage

private const val PREVIEW_ITEM_COUNT = 1
private const val CARD_BORDER_ALPHA = 0.25f
private const val CARD_FILL_ALPHA = 0.6f
private const val ADD_CARD_BORDER_ALPHA = 0.2f
private const val ADD_CARD_FILL_ALPHA = 0.35f

/**
 * Live preview of the widget stack; the cards are sized so one and a half show: hold a card and drag it sideways
 * to reorder, tap one to select it, ✕ removes it and the "+" card at the end adds a widget. The position bar below
 * follows the scroll.
 */
@Composable
fun EditWidgetGroupPreviewCarousel(
    params: EditWidgetCarouselParams,
    modifier: Modifier = Modifier
) {
    val widgets = params.currentWidgets
    val spacing = AureoleDS.dimens.small
    val margin = AureoleDS.dimens.medium
    BoxWithConstraints(modifier = modifier) {
        val cardWidth = widgetCardWidth(maxWidth.value, margin.value, spacing.value).dp
        val cardSize = DpSize(cardWidth, widgetCardHeight(cardWidth.value).dp)
        val slotPx = with(LocalDensity.current) { (cardWidth + spacing).toPx() }
        val scrollState = params.scrollState
        val widgetCount = widgets.size
        val scrolledPage by remember(scrollState, slotPx, widgetCount) {
            derivedStateOf { widgetPreviewPage(scrollState.value, slotPx, widgetCount) }
        }
        val currentPage = widgets.indexOf(params.selectedWidgetId).takeIf { it >= 0 } ?: scrolledPage
        RevealSelectedCard(params = params, slotPx = slotPx, cardPx = cardWidth, margin = margin, viewport = maxWidth)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    contentPadding = PaddingValues(horizontal = margin)
                ),
                modifier = Modifier.fillMaxWidth(),
                scrollState = params.scrollState,
                trailingContent = { AddWidgetCard(size = cardSize, onClick = params.onAddWidgetClick) }
            ) { widgetId ->
                WidgetPreviewCard(
                    widgetId = widgetId,
                    appWidgetHost = params.appWidgetHost,
                    isCurrent = widgets.indexOf(widgetId) == currentPage,
                    size = cardSize,
                    onSelect = { params.onSelectWidget(widgetId) }
                )
            }

            Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

            if (widgets.isNotEmpty() && params.isIndicatorEnabled) {
                WidgetPositionIndicatorBar(pageCount = widgets.size, currentPage = currentPage)
            }
        }
    }
}

/** After a move by button, scrolls the row just enough to keep the selected card whole inside it. */
@Composable
private fun RevealSelectedCard(
    params: EditWidgetCarouselParams,
    slotPx: Float,
    cardPx: Dp,
    margin: Dp,
    viewport: Dp
) {
    val density = LocalDensity.current
    val index = params.currentWidgets.indexOf(params.selectedWidgetId)
    LaunchedEffect(params.revealToken) {
        if (index < 0 || params.revealToken == 0) return@LaunchedEffect
        val target = with(density) {
            revealScrollTarget(
                current = params.scrollState.value,
                params = RevealScrollParams(
                    index = index,
                    slotPx = slotPx,
                    cardPx = cardPx.toPx(),
                    marginPx = margin.toPx(),
                    viewportPx = viewport.toPx()
                )
            )
        }
        params.scrollState.animateScrollTo(target)
    }
}

/** One widget of the stack; the card of the page the position bar shows gets a clear border, as on the home. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WidgetPreviewCard(
    widgetId: Int,
    appWidgetHost: AppWidgetHost?,
    isCurrent: Boolean,
    size: DpSize,
    onSelect: () -> Unit
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
    val border = if (isCurrent) {
        BorderStroke(AureoleDS.dimens.borderMax, AureoleDS.colors.onSurfaceHigh)
    } else {
        BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceMedium.copy(alpha = CARD_BORDER_ALPHA))
    }
    Box(
        modifier = sharedModifier.previewCardShape(
            size = size,
            border = border,
            fill = AureoleDS.colors.surfaceVariant.copy(alpha = CARD_FILL_ALPHA),
            shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
        ).clickable(onClick = onSelect),
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
private fun AddWidgetCard(size: DpSize, onClick: () -> Unit) {
    val label = stringResource(R.string.home_widget_add)
    val border = BorderStroke(
        AureoleDS.dimens.borderMinimal,
        AureoleDS.colors.outline.copy(alpha = ADD_CARD_BORDER_ALPHA)
    )
    Box(
        modifier = Modifier
            .previewCardShape(
                size = size,
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
private fun Modifier.previewCardShape(size: DpSize, border: BorderStroke, fill: Color, shape: Shape): Modifier = this
    .size(size)
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
