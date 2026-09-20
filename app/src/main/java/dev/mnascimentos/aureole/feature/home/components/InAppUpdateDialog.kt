package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UpdateAvailableDialog(
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nova atualização disponível",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Uma nova versão do Aureole Launcher está disponível na Google Play. Deseja atualizar agora?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmUpdate) {
                Text(
                    text = "Atualizar agora",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Agora não")
            }
        }
    )
}

@Composable
fun UpdateDownloadedDialog(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Atualização pronta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "A nova versão foi baixada com sucesso. Reinicie o aplicativo para concluir a instalação.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmRestart) {
                Text(
                    text = "Reiniciar agora",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Depois")
            }
        }
    )
}
