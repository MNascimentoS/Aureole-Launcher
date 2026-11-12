package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.resolvePreviewConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

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
    val isLeftHanded = LocalHomeUiState.current.isLeftHandedMode
    AureoleLauncherTheme(config = resolvePreviewConfig(params.base, params.pending)) {
        AureoleEditOverlay(
            modifier = modifier,
            state = overlayState,
            onDismissRequest = params.onDismissRequest,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(title = params.title, isLeftHanded = isLeftHanded),
                previewContent = {
                    AureoleMiniHome(
                        content = sampleMiniHomeContent(),
                        isLeftHanded = isLeftHanded,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                bottomBarContent = {
                    ThemePreviewActions(
                        isLeftHanded = isLeftHanded,
                        onCancel = { overlayState.close() },
                        onApply = {
                            params.onApply()
                            overlayState.close()
                        }
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

/** Cancel on the side away from the thumb, Apply (primary) on the thumb side. */
@Composable
private fun ThemePreviewActions(
    isLeftHanded: Boolean,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeftHanded) {
            ApplyButton(onClick = onApply)
            CancelButton(onClick = onCancel)
        } else {
            CancelButton(onClick = onCancel)
            ApplyButton(onClick = onApply)
        }
    }
}

@Composable
private fun CancelButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
    ) {
        AureoleText(stringResource(R.string.core_common_cancel), color = AureoleDS.colors.onSurfaceMedium)
    }
}

@Composable
private fun ApplyButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget),
        colors = ButtonDefaults.buttonColors(
            containerColor = AureoleDS.colors.onSurfaceHigh,
            contentColor = AureoleDS.colors.surface
        )
    ) {
        AureoleText(stringResource(R.string.settings_appearance_apply), color = AureoleDS.colors.surface)
    }
}
