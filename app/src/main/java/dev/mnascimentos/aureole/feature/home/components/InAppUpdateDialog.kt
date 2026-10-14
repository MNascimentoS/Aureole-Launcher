package dev.mnascimentos.aureole.feature.home.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
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
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AureoleText(
                        text = "Nova atualização disponível",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleTheme.colors.onSurfaceHigh
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AureoleText(
                        text = "Uma nova versão do Aureole Launcher está disponível na Google Play. Deseja atualizar agora?",
                        style = AureoleTheme.typography.bodyMedium,
                        color = AureoleTheme.colors.onSurfaceMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            AureoleText(text = "Agora não", color = AureoleTheme.colors.onSurfaceMedium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onConfirmUpdate) {
                            AureoleText(
                                text = "Atualizar agora",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    } else {
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
            }
        )
    }
}

@Composable
fun UpdateDownloadedDialog(
    onConfirmRestart: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AureoleText(
                        text = "Atualização pronta",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleTheme.colors.onSurfaceHigh
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AureoleText(
                        text = "A nova versão foi baixada com sucesso. Reinicie o aplicativo para concluir a instalação.",
                        style = AureoleTheme.typography.bodyMedium,
                        color = AureoleTheme.colors.onSurfaceMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            AureoleText(text = "Depois", color = AureoleTheme.colors.onSurfaceMedium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onConfirmRestart) {
                            AureoleText(
                                text = "Reiniciar agora",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    } else {
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
            }
        )
    }
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
