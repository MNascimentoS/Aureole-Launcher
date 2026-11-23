package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import dev.mnascimentos.aureole.composable.clock.scaledLayout
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.showsHomeSheetLogo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.defaultClockConfig
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.editingClockSize
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.forClockEditor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toHeaderConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockPreviewScale
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockGreeting
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPickerOpeners
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPickersParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

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
    // The editor shows and changes the clock being edited: its clock fields hold that clock's look.
    val clockState = remember(uiState) { uiState.forClockEditor() }
    var isVisible by rememberEditorVisibility(onDismissRequest)

    val savedGreeting = clockState.clockCustomGreeting
    var tempCustomGreeting by remember(savedGreeting) { mutableStateOf(savedGreeting) }
    val pagerState = rememberClockPagerState(options.initialTab)
    val effectiveHazeState = if (clockState.isHazeEnabled) options.hazeState else null
    var isFontPickerOpen by remember { mutableStateOf(false) }
    var colorPickerRole by remember { mutableStateOf<ComponentColorRole?>(null) }
    val preview: @Composable BoxScope.() -> Unit = {
        EditClockPreviewCard(
            uiState = clockState,
            tempCustomGreeting = tempCustomGreeting,
            hazeState = effectiveHazeState
        )
    }

    // The font and color pickers open over the editor (RF-B2, F2): both fill the screen, the picker on top.
    Box(modifier = modifier.fillMaxSize()) {
        AureoleEditOverlay(
            onDismissRequest = { isVisible = false },
            state = options.overlayState,
            params = AureoleEditOverlayParams(
                config = clockOverlayConfig(clockState, actions, effectiveHazeState, isVisible),
                previewContent = preview,
                bottomBarContent = { EditClockFooter(pagerState = pagerState) },
                sheetContent = {
                    EditClockPager(
                        pagerState = pagerState,
                        params = EditClockPagerParams(
                            uiState = clockState,
                            actions = actions,
                            isExpanded = options.overlayState.isExpanded,
                            greeting = EditClockGreeting(tempCustomGreeting) {
                                tempCustomGreeting = it
                                actions.onUpdateClockCustomGreeting(it)
                            },
                            nowMillis = options.nowMillis,
                            pickers = EditClockPickerOpeners(
                                onFont = { isFontPickerOpen = true },
                                onColor = { colorPickerRole = it }
                            )
                        )
                    )
                }
            )
        )
        EditClockPickers(
            params = EditClockPickersParams(clockState, actions, options.nowMillis, preview),
            isFontOpen = isFontPickerOpen,
            colorRole = colorPickerRole,
            onFontDismiss = { isFontPickerOpen = false },
            onColorDismiss = { colorPickerRole = null }
        )
    }
}

@Composable
private fun clockOverlayConfig(
    clockState: MainUiState,
    actions: HomeScreenActions,
    hazeState: HazeState?,
    isVisible: Boolean
) = AureoleEditOverlayConfig(
    title = stringResource(R.string.home_clock_customize_title),
    onSettingsClick = { actions.onSettingsClick() },
    showHeaderAction = showsHomeSheetLogo(),
    hazeState = hazeState,
    isVisible = isVisible,
    isLeftHanded = clockState.isLeftHandedMode,
    edgeToEdgeContent = true,
    hasTabBottomBar = true
)

/** The font picker and the color picker, each shown over the editor while open (RF-B2, F2). */
@Composable
private fun EditClockPickers(
    params: EditClockPickersParams,
    isFontOpen: Boolean,
    colorRole: ComponentColorRole?,
    onFontDismiss: () -> Unit,
    onColorDismiss: () -> Unit
) {
    if (isFontOpen) {
        EditClockFontPicker(
            uiState = params.uiState,
            actions = params.actions,
            onDismissRequest = onFontDismiss,
            preview = params.preview,
            nowMillis = params.nowMillis
        )
    }
    colorRole?.let { role ->
        EditClockColorPicker(
            uiState = params.uiState,
            actions = params.actions,
            role = role,
            onDismissRequest = onColorDismiss,
            preview = params.preview
        )
    }
}

/** Whether the editor shows: setting it to false slides it out, then [onDismissRequest] runs. */
@Composable
private fun rememberEditorVisibility(onDismissRequest: () -> Unit): MutableState<Boolean> {
    val isVisible = remember { mutableStateOf(true) }
    LaunchedEffect(isVisible.value) {
        if (!isVisible.value) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }
    return isVisible
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

/** Footer: tabs fixed below the preview; the margin around the bar comes from `hasTabBottomBar`. */
@Composable
private fun ColumnScope.EditClockFooter(
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

/**
 * The clock being edited, drawn as on the home (RF-B5): at the size it has there, shrunk if it does not fit above
 * the sheet, with the real glass opacity (RF-B6). Never measured yet: it fills the width, as before.
 */
@Composable
private fun EditClockPreviewCard(
    uiState: MainUiState,
    tempCustomGreeting: String,
    hazeState: HazeState? = null
) {
    val homeSize = uiState.editingClockSize
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        val clockModifier = homeSize?.let { size ->
            val scale = clockPreviewScale(size.width.value, size.height.value, maxWidth.value, maxHeight.value)
            Modifier
                .scaledLayout(scale)
                .width(size.width)
                .heightIn(max = size.height)
        } ?: Modifier
        HomeClockHeader(
            modifier = clockModifier,
            hazeConfig = ClockHazeConfig(
                isHazeEnabled = uiState.isHazeEnabled,
                hazeState = hazeState,
                hazeOpacity = uiState.hazeOpacity,
                isBackgroundEnabled = uiState.isClockBackgroundEnabled
            ),
            // The clock fields of uiState hold the edited clock (forClockEditor); all of them, the background too.
            config = uiState.defaultClockConfig().toHeaderConfig().copy(customGreeting = tempCustomGreeting)
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
