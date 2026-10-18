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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.model.UpdateDialogContentParams

private val DIALOG_CORNER_RADIUS = 22.dp

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
            title = "Nova atualização disponível",
            body = "Uma nova versão do Aureole Launcher está disponível na Google Play. " +
                "Deseja atualizar agora?",
            confirmLabel = "Atualizar agora",
            dismissLabel = "Agora não",
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
            title = "Atualização pronta",
            body = "A nova versão foi baixada com sucesso. " +
                "Reinicie o aplicativo para concluir a instalação.",
            confirmLabel = "Reiniciar agora",
            dismissLabel = "Depois",
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(DIALOG_CORNER_RADIUS),
            color = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceHigh
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                AureoleText(
                    text = params.title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                AureoleText(
                    text = params.body,
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = params.onDismiss) {
                        AureoleText(text = params.dismissLabel, color = AureoleDS.colors.onSurfaceMedium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
        shape = RoundedCornerShape(DIALOG_CORNER_RADIUS),
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
