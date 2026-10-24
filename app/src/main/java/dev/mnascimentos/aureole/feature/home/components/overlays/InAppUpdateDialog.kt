package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.model.UpdateDialogContentParams
import dev.mnascimentos.aureole.feature.home.components.overlays.model.UpdateDialogMetrics

/**
 * Dialog alerting the user that a new app update is available in Google Play.
 */
@Composable
fun UpdateAvailableDialog(
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit,
) {
    UpdateDialogContent(
        UpdateDialogContentParams(
            title = stringResource(R.string.update_new_available),
            body = stringResource(R.string.update_new_body),
            confirmLabel = stringResource(R.string.update_now),
            dismissLabel = stringResource(R.string.update_later),
            onConfirm = onConfirmUpdate,
            onDismiss = onDismiss
        )
    )
}

/**
 * Dialog alerting the user that an app update has been downloaded and is ready to install.
 */
@Composable
fun UpdateDownloadedDialog(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    UpdateDialogContent(
        UpdateDialogContentParams(
            title = stringResource(R.string.update_ready),
            body = stringResource(R.string.update_ready_body),
            confirmLabel = stringResource(R.string.update_restart_now),
            dismissLabel = stringResource(R.string.update_restart_later),
            onConfirm = onConfirmRestart,
            onDismiss = onDismiss
        )
    )
}

@Composable
private fun UpdateDialogContent(params: UpdateDialogContentParams) {
    if (LocalInspectionMode.current) {
        InspectionUpdateDialogContent(params)
    } else {
        StandardUpdateAlertDialog(params)
    }
}

@Composable
private fun InspectionUpdateDialogContent(params: UpdateDialogContentParams) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(UpdateDialogMetrics.DialogCornerRadius),
            color = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceHigh
        ) {
            Column(modifier = Modifier.padding(AureoleDS.dimens.large)) {
                AureoleText(
                    text = params.title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
                AureoleText(
                    text = params.body,
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
                )
                Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = params.onDismiss) {
                        AureoleText(text = params.dismissLabel, color = AureoleDS.colors.onSurfaceMedium)
                    }
                    Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
                    TextButton(onClick = params.onConfirm) {
                        AureoleText(
                            text = params.confirmLabel,
                            color = AureoleDS.colors.onSurfaceHigh,
                            format = AureoleTextFormatParams(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun StandardUpdateAlertDialog(params: UpdateDialogContentParams) {
    AlertDialog(
        onDismissRequest = params.onDismiss,
        containerColor = AureoleDS.colors.surface,
        shape = RoundedCornerShape(UpdateDialogMetrics.DialogCornerRadius),
        title = {
            AureoleText(
                text = params.title,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            AureoleText(
                text = params.body,
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        },
        confirmButton = {
            TextButton(onClick = params.onConfirm) {
                AureoleText(
                    text = params.confirmLabel,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(fontWeight = FontWeight.SemiBold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = params.onDismiss) {
                AureoleText(text = params.dismissLabel, color = AureoleDS.colors.onSurfaceMedium)
            }
        }
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
