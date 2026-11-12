package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

internal val PRESET_TEXT_COLORS = listOf(
    Pair(R.string.home_clock_color_default, 0),
    Pair(R.string.home_clock_color_ice_blue, 0xFF90CAF9.toInt()),
    Pair(R.string.home_clock_color_lavender, 0xFFE1BEE7.toInt()),
    Pair(R.string.home_clock_color_white, 0xFFFFFFFF.toInt()),
    Pair(R.string.home_clock_color_gold, 0xFFFFE082.toInt()),
    Pair(R.string.home_clock_color_mint, 0xFFA5D6A7.toInt()),
    Pair(R.string.home_clock_color_coral, 0xFFFFAB91.toInt())
)

internal val PRESET_BG_COLORS = listOf(
    Pair(R.string.home_clock_color_default, 0),
    Pair(R.string.home_clock_bg_color_dark_slate, 0xFF1E2124.toInt()),
    Pair(R.string.home_clock_bg_color_deep_purple, 0xFF281C74.toInt()),
    Pair(R.string.home_clock_bg_color_night_blue, 0xFF1A237E.toInt()),
    Pair(R.string.home_clock_bg_color_wine, 0xFF4A148C.toInt()),
    Pair(R.string.home_clock_bg_color_dark_green, 0xFF1B5E20.toInt())
)

private const val DISMISS_DELAY_MS = 300L

@Composable
fun EditClockBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val overlayState = rememberAureoleEditOverlayState()
    EditClockOverlay(
        uiState = uiState,
        actions = actions,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        options = remember(hazeState, overlayState) { EditClockOverlayOptions(hazeState, overlayState) }
    )
}

/** Clock editor overlay; [options] carries the blur source and the sheet state. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditClockOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    options: EditClockOverlayOptions = EditClockOverlayOptions()
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    var tempCustomGreeting by remember(uiState.clockCustomGreeting) { mutableStateOf(uiState.clockCustomGreeting) }
    val pagerState = rememberClockPagerState(options.initialTab)
    val effectiveHazeState = if (uiState.isHazeEnabled) options.hazeState else null

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        state = options.overlayState,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.home_clock_customize_title),
                onSettingsClick = { actions.onSettingsClick() },
                hazeState = effectiveHazeState,
                isVisible = isVisible,
                isLeftHanded = uiState.isLeftHandedMode,
                edgeToEdgeContent = true
            ),
            previewContent = {
                EditClockPreviewCard(
                    uiState = uiState,
                    tempCustomGreeting = tempCustomGreeting,
                    hazeState = effectiveHazeState
                )
            },
            bottomBarContent = {
                EditClockFooter(
                    pagerState = pagerState,
                    actions = actions,
                    isLeftHanded = uiState.isLeftHandedMode,
                    onSave = { isVisible = false },
                    onResetGreeting = { tempCustomGreeting = "" }
                )
            },
            sheetContent = {
                EditClockPager(
                    pagerState = pagerState,
                    params = EditClockPagerParams(
                        uiState = uiState,
                        actions = actions,
                        isExpanded = options.overlayState.isExpanded,
                        tempCustomGreeting = tempCustomGreeting,
                        onGreetingChange = {
                            tempCustomGreeting = it
                            actions.onUpdateClockCustomGreeting(it)
                        },
                        nowMillis = options.nowMillis
                    )
                )
            }
        )
    )
}

/** Pager state for the tabs; ticks the haptics every time the page changes. */
@Composable
private fun rememberClockPagerState(initialTab: ClockTab): PagerState {
    val pagerState = rememberPagerState(initialPage = initialTab.index) { ClockTab.pageCount }
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.drop(1).collect {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }
    return pagerState
}

/** Footer: tabs fixed above the action buttons. */
@Composable
private fun ColumnScope.EditClockFooter(
    pagerState: PagerState,
    actions: HomeScreenActions,
    isLeftHanded: Boolean,
    onSave: () -> Unit,
    onResetGreeting: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // The divider above the footer is drawn by the overlay sheet itself.
    Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
    EditClockTabBar(
        selected = ClockTab.resolve(
            currentPage = pagerState.currentPage,
            targetPage = pagerState.targetPage,
            isScrolling = pagerState.isScrollInProgress
        ),
        onTabSelected = { tab -> scope.launch { pagerState.animateScrollToPage(tab.index) } }
    )
    Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
    EditClockActionButtons(
        actions = actions,
        isLeftHanded = isLeftHanded,
        onDismissRequest = onSave,
        onResetGreeting = onResetGreeting
    )
}

@Composable
private fun EditClockPreviewCard(
    uiState: MainUiState,
    tempCustomGreeting: String,
    hazeState: HazeState? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AureoleDS.dimens.medium)
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        HomeClockHeader(
            hazeConfig = ClockHazeConfig(
                isHazeEnabled = uiState.isHazeEnabled,
                hazeState = hazeState,
                isBackgroundEnabled = uiState.isClockBackgroundEnabled
            ),
            config = ClockHeaderConfig(
                style = uiState.clockStyle,
                customGreeting = tempCustomGreeting,
                alignment = uiState.clockAlignment,
                fontFamily = uiState.clockFontFamily,
                timeFormat = uiState.clockTimeFormat,
                dateFormat = uiState.clockDateFormat,
                textColor = uiState.clockTextColor,
                backgroundColor = uiState.clockBackgroundColor
            )
        )
    }
}

@AureolePreview
@Composable
private fun EditClockBottomSheetContentPreview() {
    val dummyActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )
    AureoleLauncherTheme {
        EditClockBottomSheet(
            uiState = MainUiState(),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
