package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.addCustomizeItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAdjustItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeOverlayParams
import dev.mnascimentos.aureole.feature.home.components.overlays.model.itemsOnHome
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

/** Lets the sheet slide out before the caller removes it. */
private const val DISMISS_DELAY_MS = 300L

/**
 * Customize sheet: "Adjust" (Layout, Palette, Font) above "Add to home". Palette and Font open the existing
 * pickers in place of the sheet and come back to it. The items [uiState] already holds on the home are dimmed.
 */
@Composable
fun CustomizeBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    var picker by remember { mutableStateOf<AppearancePicker?>(null) }
    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    CompositionLocalProvider(LocalLeftHanded provides uiState.isLeftHandedMode) {
        when (picker) {
            AppearancePicker.PALETTE -> PickPaletteBottomSheet(
                selectedPaletteName = uiState.selectedThemeName,
                onPaletteSelected = {
                    picker = null
                    actions.onSelectPalette(it.name)
                },
                onDismissRequest = { picker = null },
                modifier = modifier
            )
            AppearancePicker.FONT -> PickFontBottomSheet(
                selectedFontName = uiState.selectedFontName,
                onFontSelected = {
                    picker = null
                    actions.onSelectFont(it)
                },
                onDismissRequest = { picker = null },
                modifier = modifier
            )
            null -> CustomizeOverlay(
                uiState = uiState,
                actions = actions,
                params = CustomizeOverlayParams(
                    isVisible = isVisible,
                    onHide = { isVisible = false },
                    onPick = { picker = it }
                ),
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CustomizeOverlay(
    uiState: MainUiState,
    actions: HomeScreenActions,
    params: CustomizeOverlayParams,
    modifier: Modifier = Modifier
) {
    val onHide = params.onHide
    val onHome = remember(uiState.gridItems) { itemsOnHome(uiState.gridItems) }
    AureoleEditOverlay(
        onDismissRequest = onHide,
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.home_customize_title),
                isVisible = params.isVisible,
                isLeftHanded = uiState.isLeftHandedMode,
                onSettingsClick = {
                    actions.onSettingsClick()
                    onHide()
                }
            ),
            sheetContent = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CustomizeAdjustSection(
                        onAdjust = { item ->
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
                        onHome = onHome,
                        onAdd = { item ->
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
