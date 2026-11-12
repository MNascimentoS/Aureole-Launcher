package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.constrainHeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.pagerMinHeight
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Pages kept composed beside the visible one, so every tab is measured and the height can follow the tabs. */
private const val OFFSCREEN_PAGES = 2

/**
 * Sheet content of the clock editor: one vertically scrollable page per [ClockTab], swiped sideways.
 * The pager is as tall as the active page and eases to the height of the next one while it slides,
 * so the Style tab has no empty gap below it.
 */
@Composable
internal fun EditClockPager(
    pagerState: PagerState,
    params: EditClockPagerParams,
    modifier: Modifier = Modifier
) {
    val pageHeights = remember { mutableStateListOf<Int>().apply { repeat(ClockTab.pageCount) { add(0) } } }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .then(if (params.isExpanded) Modifier.animateContentSize() else Modifier)
            .followActivePage(pagerState, pageHeights, params.isExpanded),
        verticalAlignment = Alignment.Top,
        beyondViewportPageCount = OFFSCREEN_PAGES,
        key = { it }
    ) { page ->
        val tab = ClockTab.fromPage(page)
        val description = stringResource(
            R.string.home_clock_tab_page_description,
            stringResource(tab.labelRes)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = description }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AureoleDS.dimens.small)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { pageHeights[page] = it.height }
            ) {
                when (tab) {
                    ClockTab.STYLE -> EditClockStylePage(params)
                    ClockTab.FORMAT -> EditClockFormatPage(params)
                    ClockTab.COLORS -> EditClockColorsPage(params.uiState, params.actions)
                }
                // Room under the last control so it is not cut in half at the edge of the scroll; the
                // expanded sheet does not scroll, so the divider sits right after the content instead.
                if (!params.isExpanded) Spacer(modifier = Modifier.height(AureoleDS.dimens.xLarge))
            }
        }
    }
}

/**
 * Sizes the pager from the measured page heights (at least the tallest page at rest, see [pagerMinHeight]);
 * read at measure time, so a swipe does not recompose.
 */
private fun Modifier.followActivePage(
    pagerState: PagerState,
    pageHeights: List<Int>,
    isExpanded: Boolean
): Modifier =
    layout { measurable, constraints ->
        val followed = ClockTab.pagerHeight(pageHeights, pagerState.currentPage, pagerState.currentPageOffsetFraction)
        val wanted = maxOf(followed, pagerMinHeight(isExpanded, pageHeights.maxOrNull() ?: 0))
        val height = constraints.constrainHeight(wanted)
        val fixed = if (wanted > 0) constraints.copy(minHeight = height, maxHeight = height) else constraints
        val placeable = measurable.measure(fixed)
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

@Composable
private fun ColumnScope.EditClockStylePage(params: EditClockPagerParams) {
    EditClockStyleSection(
        uiState = params.uiState,
        actions = params.actions,
        tempCustomGreeting = params.tempCustomGreeting,
        onGreetingChange = params.onGreetingChange
    )
    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockAlignmentSection(uiState = params.uiState, actions = params.actions)
}

@Composable
private fun ColumnScope.EditClockFormatPage(params: EditClockPagerParams) {
    EditClockFormatSection(uiState = params.uiState, actions = params.actions, nowMillis = params.nowMillis)
    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockFontSection(uiState = params.uiState, actions = params.actions)
}

@Composable
private fun ColumnScope.EditClockColorsPage(uiState: MainUiState, actions: HomeScreenActions) {
    EditClockTextColorSection(uiState = uiState, actions = actions)
    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockBackgroundSection(uiState = uiState, actions = actions)
}
