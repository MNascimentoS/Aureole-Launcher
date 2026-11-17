package dev.mnascimentos.aureole.feature.settings.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.icons.Undo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.AppearanceMenuDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.backgroundSummary
import dev.mnascimentos.aureole.feature.settings.model.fontSummary
import dev.mnascimentos.aureole.feature.settings.model.nextWallpaperScaleType
import dev.mnascimentos.aureole.feature.settings.model.paletteSummary
import dev.mnascimentos.aureole.feature.settings.screens.ChangeBorderRow
import dev.mnascimentos.aureole.feature.settings.screens.GlassAndBlurRow

/**
 * The Appearance groups: "Theme", "Background" and "Cards and glass". Rows that open a sheet call
 * [dialogActions]; the host owns the sheets.
 */
@Composable
fun AppearanceSections(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    dialogActions: AppearanceMenuDialogActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        ThemeGroup(uiState = uiState, actions = actions, dialogActions = dialogActions)
        BackgroundGroup(
            uiState = uiState,
            actions = actions,
            onSetBackground = dialogActions.onShowSetBackgroundDialog
        )
        SettingsSectionGroup(label = stringResource(R.string.settings_section_cards_glass)) {
            ChangeBorderRow(cornerRadiusDp = uiState.cornerRadiusDp, onClick = dialogActions.onShowCardsGlassDialog)
            GlassAndBlurRow(uiState = uiState, onClick = dialogActions.onShowCardsGlassDialog)
        }
    }
}

@Composable
private fun ThemeGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    dialogActions: AppearanceMenuDialogActions
) {
    SettingsSectionGroup(label = stringResource(R.string.settings_section_theme)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_appearance_color_palette),
                subtitle = paletteSummary(uiState.selectedThemeName),
                icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) },
                showChevron = true,
                onClick = dialogActions.onShowPaletteDialog
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_appearance_custom_colors),
                icon = { m, _ -> SeedSwatch(color = uiState.manualSeedColor, modifier = m) },
                showChevron = true,
                onClick = { actions.onOpenColorPickerDialog() }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_appearance_change_font),
                subtitle = fontSummary(uiState.selectedFontName),
                icon = { m, c -> AureoleDS.icons.Text(modifier = m, tint = c) },
                showChevron = true,
                onClick = dialogActions.onShowFontDialog
            )
        )
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.settings_appearance_themed_icons),
                checked = uiState.isThemedAppIconsEnabled,
                onCheckedChange = { actions.onToggleThemedAppIcons() },
                icon = { m, c -> AureoleDS.icons.AppSelect(modifier = m, tint = c) }
            )
        )
    }
}

@Composable
private fun BackgroundGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onSetBackground: () -> Unit
) {
    SettingsSectionGroup(label = stringResource(R.string.settings_section_background)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_appearance_set_background),
                subtitle = stringResource(backgroundSummary(uiState.isCustomWallpaperSet)),
                icon = { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
                showChevron = true,
                onClick = onSetBackground
            )
        )
        if (uiState.isCustomWallpaperSet) {
            AureoleMenuRow(
                MenuRowParams(
                    title = stringResource(R.string.settings_appearance_image_scale),
                    subtitle = uiState.wallpaperScaleType,
                    icon = { m, c -> AureoleDS.icons.Layout(modifier = m, tint = c) },
                    onClick = { actions.onSelectWallpaperScaleType(nextWallpaperScaleType(uiState.wallpaperScaleType)) }
                )
            )
            AureoleMenuRow(
                MenuRowParams(
                    title = stringResource(R.string.settings_appearance_restore_background),
                    icon = { m, c -> AureoleDS.icons.Undo(modifier = m, tint = c) },
                    onClick = actions.onRestoreDefaultWallpaperClick
                )
            )
        }
    }
}

/** The seed color as a dot, in the icon column of "Custom colors". */
@Composable
private fun SeedSwatch(color: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(CircleShape).background(Color(color)))
}

@AureolePreview
@Composable
private fun AppearanceSectionsPreview() {
    AureoleLauncherTheme {
        AppearanceSections(
            uiState = SettingsUiState(isCustomWallpaperSet = true),
            actions = SettingsScreenActions(),
            dialogActions = AppearanceMenuDialogActions({}, {}, {}, {})
        )
    }
}
