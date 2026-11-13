package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmDialog
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Low sheet alerting the user that a new app update is available in Google Play: "Update now" on the thumb
 * side, "Not now" on the other.
 */
@Composable
fun UpdateAvailableDialog(
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleConfirmDialog(
        modifier = modifier,
        params = ConfirmDialogParams(
            title = stringResource(R.string.settings_advanced_update_new_available),
            message = stringResource(R.string.settings_advanced_update_new_body),
            confirmLabel = stringResource(R.string.settings_advanced_update_now),
            cancelLabel = stringResource(R.string.settings_advanced_update_later),
            onConfirm = onConfirmUpdate,
            onDismiss = onDismiss
        )
    )
}

/**
 * Low sheet alerting the user that an app update has been downloaded and is ready to install: "Restart now" on
 * the thumb side, "Later" on the other.
 */
@Composable
fun UpdateDownloadedDialog(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleConfirmDialog(
        modifier = modifier,
        params = ConfirmDialogParams(
            title = stringResource(R.string.settings_advanced_update_ready),
            message = stringResource(R.string.settings_advanced_update_ready_body),
            confirmLabel = stringResource(R.string.settings_advanced_update_restart_now),
            cancelLabel = stringResource(R.string.settings_advanced_update_restart_later),
            onConfirm = onConfirmRestart,
            onDismiss = onDismiss
        )
    )
}

@AureolePreview
@Composable
private fun UpdateAvailableDialogPreview() {
    AureoleLauncherTheme {
        UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
    }
}

@AureolePreview
@Composable
private fun UpdateDownloadedDialogPreview() {
    AureoleLauncherTheme {
        UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
    }
}
