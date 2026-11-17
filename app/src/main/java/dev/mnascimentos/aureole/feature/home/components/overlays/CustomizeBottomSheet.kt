package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleLogoRing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleOverlayBackdrop
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.LOGO_RING_LOGO_FRACTION_LARGE
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.addCustomizeItem
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.blockingTouches
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CUSTOMIZE_SHOWCASE_MS
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAdjustItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeOverlayParams
import dev.mnascimentos.aureole.feature.home.components.overlays.model.customizeShowcaseProgress
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

/** Lets the sheet slide out before the caller removes it. */
private const val DISMISS_DELAY_MS = 300L

/** Same fade as the backdrop of a single sheet. */
private const val BACKDROP_FADE_MS = 300

/**
 * Customize sheet: "Adjust" (Layout, Palette, Font) above "Add to home". Palette and Font open the existing
 * pickers in place of the sheet and come back to it. The items [uiState] already holds on the home are dimmed.
 *
 * With [isShowcase] (the onboarding's customize step) the same sheet is only shown: no tile responds, back,
 * drag-down and tap-outside do nothing, and the ring on the header logo fills in [CUSTOMIZE_SHOWCASE_MS], then the
 * sheet closes itself (RF-01 to RF-03). It is read once, when the sheet opens.
 */
@Composable
fun CustomizeBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isShowcase: Boolean = false
) {
    var isVisible by remember { mutableStateOf(true) }
    var picker by remember { mutableStateOf<AppearancePicker?>(null) }
    val customizeState = rememberAureoleEditOverlayState()
    val showcase = remember { isShowcase }
    val showcaseProgress = remember { mutableFloatStateOf(0f) }
    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }
    if (showcase) {
        // A frame clock, not an animation: with the system animations off it still takes the same time.
        LaunchedEffect(Unit) {
            val start = withFrameMillis { it }
            while (showcaseProgress.floatValue < 1f) {
                showcaseProgress.floatValue = customizeShowcaseProgress(withFrameMillis { it } - start)
            }
            isVisible = false
        }
    }

    // One backdrop for the Customize sheet and the pickers it opens, so swapping them does not flash the home:
    // the Customize sheet slides down while the picker slides up, and back.
    val isBackdropVisible = isVisible && (picker != null || customizeState.internalVisible)
    CompositionLocalProvider(
        LocalLeftHanded provides uiState.isLeftHandedMode
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = isBackdropVisible,
                enter = fadeIn(tween(BACKDROP_FADE_MS)),
                exit = fadeOut(tween(BACKDROP_FADE_MS))
            ) {
                AureoleOverlayBackdrop(hazeState = LocalHazeState.current, modifier = Modifier.fillMaxSize())
            }
            CustomizeOverlay(
                uiState = uiState,
                actions = actions,
                params = CustomizeOverlayParams(
                    isVisible = isVisible && picker == null,
                    onHide = { isVisible = false },
                    onPick = { picker = it },
                    showcaseProgress = if (showcase) showcaseProgress else null
                ),
                state = customizeState
            )
            CustomizePicker(
                picker = picker,
                uiState = uiState,
                actions = actions,
                onClose = { picker = null }
            )
        }
    }
}

@Composable
private fun CustomizePicker(
    picker: AppearancePicker?,
    uiState: MainUiState,
    actions: HomeScreenActions,
    onClose: () -> Unit
) {
    when (picker) {
        AppearancePicker.PALETTE -> PickPaletteBottomSheet(
            selectedPaletteName = uiState.selectedThemeName,
            onPaletteSelected = { actions.onSelectPalette(it.name) },
            onDismissRequest = onClose,
            options = ThemePickerSheetOptions(hasExternalBackdrop = true)
        )
        AppearancePicker.FONT -> PickFontBottomSheet(
            selectedFontName = uiState.selectedFontName,
            onFontSelected = { actions.onSelectFont(it) },
            onDismissRequest = onClose,
            options = ThemePickerSheetOptions(hasExternalBackdrop = true)
        )
        null -> Unit
    }
}

@Composable
private fun CustomizeOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions,
    params: CustomizeOverlayParams,
    state: AureoleEditOverlayState
) {
    val onHide = params.onHide
    val showcaseProgress = params.showcaseProgress
    val isShowcase = showcaseProgress != null
    AureoleEditOverlay(
        onDismissRequest = onHide,
        state = state,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.home_customize_title),
                isVisible = params.isVisible,
                isLeftHanded = uiState.isLeftHandedMode,
                // The logo on the title line opens the settings, as in the other home sheets (RF-05b). In the
                // showcase it is not clickable and wears the countdown ring instead.
                onSettingsClick = {
                    actions.onSettingsClick()
                    onHide()
                }.takeUnless { isShowcase },
                hasExternalBackdrop = true,
                isLocked = isShowcase,
                headerAccessory = showcaseProgress?.let { progress -> { CustomizeShowcaseRing(progress) } }
            ),
            sheetContent = {
                Column(
                    modifier = Modifier
                        .blockingTouches(isShowcase)
                        .verticalScroll(rememberScrollState(), enabled = !isShowcase)
                ) {
                    CustomizeAdjustSection(
                        onAdjust = { item ->
                            if (isShowcase) return@CustomizeAdjustSection
                            when (item) {
                                CustomizeAdjustItem.LAYOUT -> {
                                    actions.onEnterGridEditMode()
                                    onHide()
                                }
                                CustomizeAdjustItem.PALETTE -> params.onPick(AppearancePicker.PALETTE)
                                CustomizeAdjustItem.FONT -> params.onPick(AppearancePicker.FONT)
                            }
                        },
                        isLeftHanded = uiState.isLeftHandedMode
                    )
                    CustomizeAddSection(
                        onAdd = { item ->
                            if (isShowcase) return@CustomizeAddSection
                            actions.addCustomizeItem(item)
                            onHide()
                        },
                        isLeftHanded = uiState.isLeftHandedMode
                    )
                }
            }
        )
    )
}

/** The header logo inside its ring, filling as the showcase runs out; it has no click. */
@Composable
private fun CustomizeShowcaseRing(progress: FloatState) {
    // The header slot already centers it in 48 dp, on the title line.
    AureoleLogoRing(
        progress = progress.floatValue,
        modifier = Modifier.size(AureoleDS.dimens.iconXLargeMedium),
        logoFraction = LOGO_RING_LOGO_FRACTION_LARGE,
        isProgressAnimated = false
    )
}

@AureolePreview
@Composable
private fun CustomizeBottomSheetPreview() {
    val actions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )
    AureoleLauncherTheme {
        CustomizeBottomSheet(
            uiState = MainUiState(),
            actions = actions,
            onDismissRequest = {}
        )
    }
}
