package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Pages kept composed beside the visible one, so every tab is measured and the height stays stable. */
private const val OFFSCREEN_PAGES = 2

/**
 * Sheet content of the clock editor: one vertically scrollable page per [ClockTab], swiped sideways.
 * The pager is at least as tall as the tallest page, so switching tabs never moves the sheet.
 */
@Composable
internal fun EditClockPager(
    pagerState: PagerState,
    params: EditClockPagerParams,
    modifier: Modifier = Modifier
) {
    var tallestPagePx by remember { mutableIntStateOf(0) }
    val minHeight = with(LocalDensity.current) { tallestPagePx.toDp() }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight),
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { tallestPagePx = ClockTab.stableHeight(tallestPagePx, it.height) }
            ) {
                when (tab) {
                    ClockTab.STYLE -> EditClockStylePage(params)
                    ClockTab.FORMAT -> EditClockFormatPage(params.uiState, params.actions)
                    ClockTab.COLORS -> EditClockColorsPage(params.uiState, params.actions)
                }
                Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
            }
        }
    }
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
private fun ColumnScope.EditClockFormatPage(uiState: MainUiState, actions: HomeScreenActions) {
    EditClockFormatSection(uiState = uiState, actions = actions)
    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockFontSection(uiState = uiState, actions = actions)
}

@Composable
private fun ColumnScope.EditClockColorsPage(uiState: MainUiState, actions: HomeScreenActions) {
    EditClockTextColorSection(uiState = uiState, actions = actions)
    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockBackgroundSection(uiState = uiState, actions = actions)
}
