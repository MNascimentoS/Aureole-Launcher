package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun UpdateAvailableDialog(
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AureoleTheme.colors.surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            AureoleText(
                text = "Nova atualização disponível",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        text = {
            AureoleText(
                text = "Uma nova versão do Aureole Launcher está disponível na Google Play. Deseja atualizar agora?",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmUpdate) {
                AureoleText(
                    text = "Atualizar agora",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(text = "Agora não", color = AureoleTheme.colors.onSurfaceMedium)
            }
        },
        modifier = Modifier.border(
            width = 0.5.dp,
            color = AureoleTheme.colors.outline,
            shape = RoundedCornerShape(22.dp)
        )
    )
}

@Composable
fun UpdateDownloadedDialog(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AureoleTheme.colors.surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            AureoleText(
                text = "Atualização pronta",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        text = {
            AureoleText(
                text = "A nova versão foi baixada com sucesso. Reinicie o aplicativo para concluir a instalação.",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmRestart) {
                AureoleText(
                    text = "Reiniciar agora",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(text = "Depois", color = AureoleTheme.colors.onSurfaceMedium)
            }
        },
        modifier = Modifier.border(
            width = 0.5.dp,
            color = AureoleTheme.colors.outline,
            shape = RoundedCornerShape(22.dp)
        )
    )
}

@AureolePreview
@Composable
fun UpdateAvailableDialogPreview() {
    AureoleLauncherTheme {
        UpdateAvailableDialog(onConfirmUpdate = {}, onDismiss = {})
    }
}

@AureolePreview
@Composable
fun UpdateDownloadedDialogPreview() {
    AureoleLauncherTheme {
        UpdateDownloadedDialog(onConfirmRestart = {}, onDismiss = {})
    }
}
