package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Low confirmation sheet anchored at the bottom, within reach of the thumb: a question, one line of explanation
 * and the Cancel / confirm row, the confirm button on the thumb side (from the launcher setting). With
 * [ConfirmDialogParams.holdMs] the confirm button is the error colored hold button on the opposite side, so an
 * irreversible action cannot be hit by accident. Built on [AureoleConfirmOverlay].
 */
@Composable
fun AureoleConfirmDialog(
    params: ConfirmDialogParams,
    modifier: Modifier = Modifier
) {
    AureoleConfirmOverlay(
        modifier = modifier,
        params = ConfirmOverlayParams(
            title = params.title,
            primaryLabel = params.confirmLabel,
            onPrimary = params.onConfirm,
            onDismissRequest = params.onDismiss,
            isLeftHanded = LocalLeftHanded.current,
            secondaryLabel = params.cancelLabel,
            primaryHoldMs = params.holdMs,
            isPrimaryAwayFromThumb = params.holdMs != null
        )
    ) {
        AureoleText(
            text = params.message,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.small),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleConfirmDialogPreview() {
    AureoleLauncherTheme {
        AureoleConfirmDialog(
            params = ConfirmDialogParams(
                title = "Reset layout?",
                message = "Items go back to the starting position of the home screen.",
                confirmLabel = "Restore",
                onConfirm = {},
                onDismiss = {}
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleConfirmDialogHoldPreview() {
    AureoleLauncherTheme {
        AureoleConfirmDialog(
            params = ConfirmDialogParams(
                title = "Reset launcher?",
                message = "Erases all your settings and starts over. This cannot be undone.",
                confirmLabel = "Hold to reset",
                onConfirm = {},
                onDismiss = {},
                holdMs = HOLD_CONFIRM_MS
            )
        )
    }
}
