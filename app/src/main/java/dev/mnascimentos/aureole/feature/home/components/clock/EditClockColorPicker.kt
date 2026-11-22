package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorRefFor
import dev.mnascimentos.aureole.feature.home.components.clock.model.clockColorTitleRes
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/**
 * The launcher palette picker in color mode, opened by the clock editor for its text or card (RF-F2 to F4):
 * "Text color" / "Card color", "Only for this clock", "Theme" first and each palette with the tone of [role].
 * Every choice goes straight to [actions] (auto-save, like the rest of the editor), so [preview] shows it at once.
 */
@Composable
internal fun EditClockColorPicker(
    uiState: MainUiState,
    actions: HomeScreenActions,
    role: ComponentColorRole,
    onDismissRequest: () -> Unit,
    preview: (@Composable BoxScope.() -> Unit)? = null
) {
    PickPaletteBottomSheet(
        color = ComponentColorOptions(
            role = role,
            title = stringResource(clockColorTitleRes(role)),
            subtitle = stringResource(R.string.home_clock_picker_subtitle),
            selected = clockColorRefFor(uiState, role),
            themeColor = clockDefaultColor(role),
            onSelect = when (role) {
                ComponentColorRole.TEXT -> actions.onUpdateClockTextColor
                ComponentColorRole.CARD -> actions.onUpdateClockBackgroundColor
            },
            preview = preview
        ),
        onDismissRequest = onDismissRequest
    )
}

@AureolePreview
@Composable
private fun EditClockColorPickerPreview() {
    AureoleLauncherTheme {
        EditClockColorPicker(
            uiState = MainUiState(),
            actions = createDefaultHomeScreenActions(),
            role = ComponentColorRole.CARD,
            onDismissRequest = {}
        )
    }
}
