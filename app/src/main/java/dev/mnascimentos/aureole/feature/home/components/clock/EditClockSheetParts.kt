package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.components.clock.model.showsClockSaveHint
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.launch

/**
 * The header of the color page (RF-E4): back arrow, "Text color" / "Card color" and "Only for this clock". Back
 * ([onBack]) returns to the Colors tab; dragging down or tapping outside still closes the editor ([onClose]).
 */
@Composable
internal fun AureoleEditOverlayConfig.withColorPage(
    role: ComponentColorRole?,
    onBack: () -> Unit,
    onClose: () -> Unit
): AureoleEditOverlayConfig {
    if (role == null) return this
    return copy(
        title = stringResource(clockColorTitleRes(role)),
        subtitle = stringResource(R.string.home_clock_picker_subtitle),
        onBackPress = onBack,
        onDismiss = onClose,
        headerLeadingAction = { EditClockColorPageBack(onBack = onBack) },
        hasTabBottomBar = false
    )
}

/** What saves the color of [role]: the text or the card color action. */
internal fun clockColorUpdate(actions: HomeScreenActions, role: ComponentColorRole) = when (role) {
    ComponentColorRole.TEXT -> actions.onUpdateClockTextColor
    ComponentColorRole.CARD -> actions.onUpdateClockBackgroundColor
}

/** The bottom bar under the tabs; none on the color page, which hides the tab bar (RF-E5). */
internal fun clockBottomBar(
    colorRole: ComponentColorRole?,
    clockState: MainUiState,
    actions: HomeScreenActions,
    pagerState: PagerState
): (@Composable ColumnScope.() -> Unit)? = if (colorRole == null) {
    { EditClockBottomBar(clockState, actions, pagerState) }
} else {
    null
}

/** The sheet content: the color page of [colorRole] while one is open (RF-E4), else the tabs. */
@Composable
internal fun EditClockSheetBody(
    colorRole: ComponentColorRole?,
    pagerState: PagerState,
    params: EditClockPagerParams,
    onColorPicked: (ComponentColorRole, ThemeColorRef?) -> Unit
) {
    if (colorRole != null) {
        EditClockColorPage(uiState = params.uiState, role = colorRole) { onColorPicked(colorRole, it) }
    } else {
        EditClockPager(pagerState = pagerState, params = params)
    }
}

/** Below the tabs' content: the onboarding notice (RF-D2) and the tab bar. */
@Composable
internal fun ColumnScope.EditClockBottomBar(
    clockState: MainUiState,
    actions: HomeScreenActions,
    pagerState: PagerState
) {
    if (showsClockSaveHint(clockState.onboarding.stage, clockState.isClockSaveHintDismissed)) {
        EditClockSaveHint(
            onDismiss = actions.onDismissClockSaveHint,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
        )
    }
    EditClockFooter(pagerState = pagerState)
}

/** Footer: tabs fixed below the preview; the margin around the bar comes from `hasTabBottomBar`. */
@Composable
internal fun ColumnScope.EditClockFooter(
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()
    EditClockTabBar(
        selected = ClockTab.resolve(
            currentPage = pagerState.currentPage,
            targetPage = pagerState.targetPage,
            isScrolling = pagerState.isScrollInProgress
        ),
        onTabSelected = { tab -> scope.launch { pagerState.animateScrollToPage(tab.index) } }
    )
}
