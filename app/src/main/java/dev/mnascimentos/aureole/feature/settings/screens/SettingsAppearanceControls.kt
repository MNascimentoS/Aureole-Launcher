package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Glass
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.glassSummary

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
