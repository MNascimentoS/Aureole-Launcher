package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.reorder.RemoveBadge
import dev.mnascimentos.aureole.composable.widgets.WidgetPositionIndicatorBar
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.landscapeWidgetCardWidth
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetCardHeight
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetPagerPages

/**
 * The widget stack preview in landscape (RF-11): one widget per page, whole and centered, with no neighbor cut at
 * the side; it changes only sideways (swipe or the "Move" arrows, which select the widget and turn the page to
 * it), with the position dots below. The add card is the last page. Settling on a widget selects it.
 */
@Composable
internal fun EditWidgetGroupLandscapePager(
    params: EditWidgetCarouselParams,
    modifier: Modifier = Modifier
) {
    val widgets = params.currentWidgets
    val pages = widgetPagerPages(widgets.size)
    val selectedIndex = widgets.indexOf(params.selectedWidgetId)
    val pagerState = rememberPagerState(initialPage = selectedIndex.coerceAtLeast(0)) { pages }
    val latest by rememberUpdatedState(params)
    LaunchedEffect(selectedIndex, params.revealToken) {
        if (selectedIndex >= 0 && pagerState.currentPage != selectedIndex) pagerState.animateScrollToPage(selectedIndex)
    }
    LaunchedEffect(pagerState.settledPage) {
        val settled = latest.currentWidgets.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
        if (settled != latest.selectedWidgetId) latest.onSelectWidget(settled)
    }
    val margin = AureoleDS.dimens.medium
    BoxWithConstraints(modifier = modifier) {
        val dotsRoom = AureoleDS.dimens.medium + AureoleDS.dimens.large
        val cardWidth = landscapeWidgetCardWidth(
            rowWidth = maxWidth.value,
            margin = margin.value,
            maxCardHeight = if (constraints.hasBoundedHeight) (maxHeight - dotsRoom).value else Float.POSITIVE_INFINITY
        ).dp
        val cardSize = DpSize(cardWidth, widgetCardHeight(cardWidth.value).dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = margin),
                pageSpacing = margin,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val widgetId = widgets.getOrNull(page)
                    if (widgetId == null) {
                        AddWidgetCard(size = cardSize, onClick = params.onAddWidgetClick)
                    } else {
                        WidgetPagerCard(widgetId = widgetId, size = cardSize, params = params)
                    }
                }
            }
            Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
            WidgetPositionIndicatorBar(pageCount = pages, currentPage = pagerState.currentPage)
        }
    }
}

/** A widget page: the card, current like on the home, and its ✕ at the top corner. */
@Composable
private fun WidgetPagerCard(widgetId: Int, size: DpSize, params: EditWidgetCarouselParams) {
    Box {
        WidgetPreviewCard(
            widgetId = widgetId,
            appWidgetHost = params.appWidgetHost,
            isCurrent = true,
            size = size,
            onSelect = { params.onSelectWidget(widgetId) }
        )
        RemoveBadge(
            onClick = { params.onRemoveWidget(widgetId) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = AureoleDS.dimens.xSmall, y = -AureoleDS.dimens.xSmall)
        )
    }
}

private const val PREVIEW_FIRST_ID = 1
private const val PREVIEW_SELECTED_ID = 2
private const val PREVIEW_LAST_ID = 3

@AureolePreview
@Composable
private fun EditWidgetGroupLandscapePagerPreview() {
    AureoleLauncherTheme {
        EditWidgetGroupLandscapePager(
            params = EditWidgetCarouselParams(
                currentWidgets = listOf(PREVIEW_FIRST_ID, PREVIEW_SELECTED_ID, PREVIEW_LAST_ID),
                appWidgetHost = null,
                scrollState = rememberScrollState(),
                isIndicatorEnabled = true,
                onRemoveWidget = {},
                onAddWidgetClick = {},
                selectedWidgetId = PREVIEW_SELECTED_ID
            )
        )
    }
}
