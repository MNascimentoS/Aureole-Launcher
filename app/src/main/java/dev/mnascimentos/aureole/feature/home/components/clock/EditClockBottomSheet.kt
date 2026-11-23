package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.isClockBackgroundEnabled
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toHeaderConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockGreeting
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPagerParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockPickerOpeners
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockPreviewScale
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop

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
    var colorPickerRole by remember { mutableStateOf(options.initialColorRole) }
    val preview = clockPreview(clockState, tempCustomGreeting, effectiveHazeState)
    val greeting = EditClockGreeting(tempCustomGreeting) {
        tempCustomGreeting = it
        actions.onUpdateClockCustomGreeting(it)
    }
    val pickers = EditClockPickerOpeners(onFont = { isFontPickerOpen = true }, onColor = { colorPickerRole = it })

    val colorRole = colorPickerRole
    // The color page replaces the tabs inside this sheet (RF-E4, D-02); the font picker opens over it (RF-B2).
    Box(modifier = modifier.fillMaxSize()) {
        AureoleEditOverlay(
            onDismissRequest = { isVisible = false },
            state = options.overlayState,
            params = AureoleEditOverlayParams(
                config = clockOverlayConfig(clockState, actions, effectiveHazeState, isVisible).withColorPage(
                    role = colorRole,
                    onBack = { colorPickerRole = null },
                    onClose = { isVisible = false }
                ),
                previewContent = preview,
                bottomBarContent = clockBottomBar(colorRole, clockState, actions, pagerState),
                sheetContent = {
                    EditClockSheetBody(
                        colorRole = colorRole,
                        pagerState = pagerState,
                        params = options.pagerParams(clockState, actions, greeting, pickers),
                        onColorPicked = { role, picked ->
                            // Applied at once, then back to the Colors tab (RF-E5).
                            clockColorUpdate(actions, role)(picked)
                            colorPickerRole = null
                        }
                    )
                }
            )
        )
        if (isFontPickerOpen) {
            EditClockFontPicker(
                uiState = clockState,
                actions = actions,
                onDismissRequest = { isFontPickerOpen = false },
                preview = preview,
                nowMillis = options.nowMillis
            )
        }
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
    hasTabBottomBar = true,
    // No save button (D-06): the sheet closes by dragging, so the handle shows when a release would close it.
    showsDismissFeedback = true
)

/** The clock being edited above the sheet, with the message being typed. */
private fun clockPreview(
    clockState: MainUiState,
    tempCustomGreeting: String,
    hazeState: HazeState?
): @Composable BoxScope.() -> Unit = {
    EditClockPreviewCard(uiState = clockState, tempCustomGreeting = tempCustomGreeting, hazeState = hazeState)
}

/** What the pages of the editor show and change. */
private fun EditClockOverlayOptions.pagerParams(
    clockState: MainUiState,
    actions: HomeScreenActions,
    greeting: EditClockGreeting,
    pickers: EditClockPickerOpeners
) = EditClockPagerParams(
    uiState = clockState,
    actions = actions,
    isExpanded = overlayState.isExpanded,
    greeting = greeting,
    nowMillis = nowMillis,
    pickers = pickers
)

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
