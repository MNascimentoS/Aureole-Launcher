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

@Suppress("LongMethod")
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

data class UpdateDialogContentParams(
    val title: String,
    val body: String,
    val confirmLabel: String,
    val dismissLabel: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)

@Suppress("LongMethod")
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

@Suppress("LongMethod")
@Composable
private fun UpdateDialogContent(params: UpdateDialogContentParams) {
    val title = params.title
    val body = params.body
    val confirmLabel = params.confirmLabel
    val dismissLabel = params.dismissLabel
    val onConfirm = params.onConfirm
    val onDismiss = params.onDismiss

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = AureoleDS.colors.surface,
                contentColor = AureoleDS.colors.onSurfaceHigh
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AureoleText(
                        text = title,
                        color = AureoleDS.colors.onSurfaceHigh,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AureoleText(
                        text = body,
                        color = AureoleDS.colors.onSurfaceMedium,
                        format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            AureoleText(text = dismissLabel, color = AureoleDS.colors.onSurfaceMedium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onConfirm) {
                            AureoleText(
                                text = confirmLabel,
                                color = AureoleDS.colors.onSurfaceHigh,
                                format = AureoleTextFormatParams(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = AureoleDS.colors.surface,
            shape = RoundedCornerShape(22.dp),
            title = {
                AureoleText(
                    text = title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                AureoleText(
                    text = body,
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    AureoleText(
                        text = confirmLabel,
                        color = AureoleDS.colors.onSurfaceHigh,
                        format = AureoleTextFormatParams(fontWeight = FontWeight.SemiBold)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    AureoleText(text = dismissLabel, color = AureoleDS.colors.onSurfaceMedium)
                }
            }
        )
    }
}

@AureolePreview
@Suppress("LongMethod")
@Composable
private fun UpdateAvailableDialogPreview() {
    AureoleLauncherTheme {
        UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
    }
}

@AureolePreview
@Suppress("LongMethod")
@Composable
private fun UpdateDownloadedDialogPreview() {
    AureoleLauncherTheme {
        UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
    }
}
