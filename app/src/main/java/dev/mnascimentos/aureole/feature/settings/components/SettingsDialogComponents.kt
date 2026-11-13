package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleRadioSheet
import dev.mnascimentos.aureole.composable.menu.model.RadioOption
import dev.mnascimentos.aureole.composable.menu.model.RadioSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.PositionKind
import dev.mnascimentos.aureole.feature.settings.model.options

@Composable
internal fun SettingsButtonPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    SettingsPositionSheet(
        title = stringResource(R.string.settings_dialogs_settings_button_position_title),
        kind = PositionKind.SETTINGS_BUTTON,
        currentPosition = currentPosition,
        onPositionSelected = onPositionSelected,
        onDismiss = onDismiss
    )
}

@Composable
internal fun SearchIconPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    SettingsPositionSheet(
        title = stringResource(R.string.settings_dialogs_search_icon_position_title),
        kind = PositionKind.SEARCH_ICON,
        currentPosition = currentPosition,
        onPositionSelected = onPositionSelected,
        onDismiss = onDismiss
    )
}

@Composable
internal fun ContainerPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    SettingsPositionSheet(
        title = stringResource(R.string.settings_dialogs_container_position_title),
        kind = PositionKind.CONTAINER,
        currentPosition = currentPosition,
        onPositionSelected = onPositionSelected,
        onDismiss = onDismiss
    )
}

@Composable
private fun SettingsPositionSheet(
    title: String,
    kind: PositionKind,
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AureoleRadioSheet(
        params = RadioSheetParams(
            title = title,
            options = kind.options().map { RadioOption(it.value, stringResource(it.labelRes)) },
            selectedValue = currentPosition
        ),
        onSelect = onPositionSelected,
        onDismiss = onDismiss
    )
}

@AureolePreview
@Composable
private fun ContainerPositionDialogPreview() {
    AureoleLauncherTheme {
        ContainerPositionDialog(currentPosition = "Center", onPositionSelected = {}, onDismiss = {})
    }
}

@AureolePreview
@Composable
private fun SettingsButtonPositionDialogPreview() {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(currentPosition = "Right", onPositionSelected = {}, onDismiss = {})
    }
}

@AureolePreview
@Composable
private fun SearchIconPositionDialogPreview() {
    AureoleLauncherTheme {
        SearchIconPositionDialog(currentPosition = "Left", onPositionSelected = {}, onDismiss = {})
    }
}
