package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.resolvePreviewConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Overlay shared by the palette and font sheets: a mini-home above the sheet and nothing below it. The mini-home
 * and the sheet are drawn in a local theme resolved from [params], so a pending choice shows at once without being
 * persisted. Closing the sheet (tap outside, back, swipe down) calls `onCommit` when the choice differs from the
 * current theme.
 */
@Composable
internal fun ThemePreviewOverlay(
    params: ThemePreviewOverlayParams,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isLeftHanded = LocalLeftHanded.current
    AureoleLauncherTheme(config = resolvePreviewConfig(params.base, params.pending)) {
        AureoleAutoSaveOverlay(
            params = AutoSaveOverlayParams(
                title = params.title,
                isChanged = params.isChanged,
                onCommit = params.onCommit,
                onDismissRequest = params.onDismissRequest,
                isLeftHanded = isLeftHanded,
                onSettingsClick = params.onSettingsClick
            ),
            modifier = modifier,
            preview = {
                AureoleMiniHome(
                    content = sampleMiniHomeContent(),
                    isLeftHanded = isLeftHanded,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            content = content
        )
    }
}

@AureolePreview
@Composable
private fun ThemePreviewOverlayPreview() {
    AureoleLauncherTheme {
        ThemePreviewOverlay(
            params = ThemePreviewOverlayParams(
                title = stringResource(R.string.settings_appearance_pick_palette_title),
                base = AureoleThemeConfig(darkTheme = false),
                pending = ThemePreviewState(),
                isChanged = { false },
                onCommit = {},
                onDismissRequest = {}
            )
        ) {
            AureoleText(
                text = stringResource(R.string.settings_appearance_palette_section_label),
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}
