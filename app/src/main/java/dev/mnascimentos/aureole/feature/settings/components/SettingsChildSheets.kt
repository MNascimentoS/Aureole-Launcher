package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.sheets.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft

/**
 * The sheet an area opens in its place (the palette and font pickers open inside the Appearance sheet instead).
 * Each one saves on close as it always did (with its Undo) and calls [onClose] once it has slid out, which brings
 * the area back.
 */
@Composable
internal fun SettingsChildSheet(
    kind: SettingsChildKind,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onClose: () -> Unit
) {
    when (kind) {
        SettingsChildKind.BACKGROUND -> SetBackgroundBottomSheet(
            params = SetBackgroundParams(
                currentColor = uiState.manualSeedColor,
                isCustomImageSet = uiState.isCustomWallpaperSet,
                onSolidColorSelected = actions.onSelectBackgroundColor,
                onCustomImageClick = actions.onChangeWallpaperClick,
                onDismissRequest = onClose,
                isLeftHanded = uiState.isLeftHandedMode
            )
        )
        SettingsChildKind.CARDS_GLASS -> CardsGlassBottomSheet(
            params = CardsGlassParams(
                initial = uiState.toCardsGlassDraft(),
                onChange = actions.onApplyCardsGlass,
                onCommit = actions.onCardsGlassClosed,
                onDismissRequest = onClose,
                isLeftHanded = uiState.isLeftHandedMode
            )
        )
        SettingsChildKind.RELEASE_NOTES -> ReleaseNotesBottomSheet(onDismissRequest = onClose)
        SettingsChildKind.CONTRIBUTORS -> ContributorsBottomSheet(onDismissRequest = onClose)
    }
}

@AureolePreview
@Composable
private fun SettingsChildSheetPreview() {
    AureoleLauncherTheme {
        SettingsChildSheet(
            kind = SettingsChildKind.CONTRIBUTORS,
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onClose = {}
        )
    }
}
