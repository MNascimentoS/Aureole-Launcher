package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureoleActionRow
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.resolvePreviewConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Overlay shared by the palette and font sheets: a mini-home above the sheet and Cancel/Apply below it.
 * The mini-home, the sheet and the buttons are drawn in a local theme resolved from [params], so a pending
 * choice shows at once without being persisted. Apply calls `onApply` and closes; Cancel, swipe down and
 * tapping outside only close.
 */
@Composable
internal fun ThemePreviewOverlay(
    params: ThemePreviewOverlayParams,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val overlayState = rememberAureoleEditOverlayState()
    val isLeftHanded = LocalLeftHanded.current
    val applyLabel = stringResource(R.string.settings_appearance_apply)
    val cancelLabel = stringResource(R.string.core_common_cancel)
    AureoleLauncherTheme(config = resolvePreviewConfig(params.base, params.pending)) {
        AureoleEditOverlay(
            modifier = modifier,
            state = overlayState,
            onDismissRequest = params.onDismissRequest,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(
                    title = params.title,
                    isLeftHanded = isLeftHanded,
                    onSettingsClick = params.onSettingsClick
                ),
                previewContent = {
                    AureoleMiniHome(
                        content = sampleMiniHomeContent(),
                        isLeftHanded = isLeftHanded,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                bottomBarContent = {
                    AureoleActionRow(
                        params = ActionRowParams(
                            primaryLabel = applyLabel,
                            onPrimary = {
                                params.onApply()
                                overlayState.close()
                            },
                            secondaryLabel = cancelLabel,
                            onSecondary = { overlayState.close() }
                        ),
                        isLeftHanded = isLeftHanded
                    )
                },
                sheetContent = content
            )
        )
    }
}

private fun AureoleEditOverlayState.close() {
    internalVisible = false
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
                onApply = {},
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
