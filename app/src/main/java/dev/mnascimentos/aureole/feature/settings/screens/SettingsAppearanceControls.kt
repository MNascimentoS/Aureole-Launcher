package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Glass
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.CardsGlassBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.glassSummary
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft

@Composable
internal fun GlassAndBlurRow(
    uiState: SettingsUiState,
    onClick: () -> Unit
) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.settings_appearance_glass_effect),
            subtitle = stringResource(glassSummary(uiState)),
            icon = { m, c -> AureoleDS.icons.Glass(modifier = m, tint = c) },
            showChevron = true,
            onClick = onClick
        )
    )
}

@Composable
internal fun ChangeBorderRow(
    cornerRadiusDp: Int,
    onClick: () -> Unit
) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.settings_appearance_change_border),
            subtitle = stringResource(R.string.settings_appearance_border_radius_format, cornerRadiusDp),
            icon = { m, c -> AureoleDS.icons.Corners(modifier = m, tint = c) },
            showChevron = true,
            onClick = onClick
        )
    )
}

@Composable
internal fun AppearanceDialogs(
    flags: AppearanceDialogFlags,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onDismissDialog: () -> Unit
) {
    if (flags.showPalette) {
        PickPaletteBottomSheet(
            selectedPaletteName = uiState.selectedThemeName,
            onPaletteSelected = { palette -> actions.onSelectTheme(palette.name) },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showFont) {
        PickFontBottomSheet(
            selectedFontName = uiState.selectedFontName,
            onFontSelected = { fontName -> actions.onSelectFont(fontName) },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showSetBg) {
        SetBackgroundBottomSheet(
            params = SetBackgroundParams(
                currentColor = uiState.manualSeedColor,
                isCustomImageSet = uiState.isCustomWallpaperSet,
                onSolidColorSelected = actions.onSelectBackgroundColor,
                onCustomImageClick = actions.onChangeWallpaperClick,
                onDismissRequest = onDismissDialog,
                isLeftHanded = uiState.isLeftHandedMode
            )
        )
    }

    if (flags.showCardsGlass) {
        CardsGlassBottomSheet(
            params = CardsGlassParams(
                initial = uiState.toCardsGlassDraft(),
                onChange = actions.onApplyCardsGlass,
                onCommit = actions.onCardsGlassClosed,
                onDismissRequest = onDismissDialog,
                isLeftHanded = uiState.isLeftHandedMode
            )
        )
    }
}

@AureolePreview
@Composable
private fun SettingsAppearanceControlsPreview() {
    AureoleLauncherTheme {
        GlassAndBlurRow(
            uiState = SettingsUiState(),
            onClick = {}
        )
    }
}
