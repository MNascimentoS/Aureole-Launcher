package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

@Composable
internal fun SettingsButtonPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Left" to "Esquerda (Left)",
        "Right" to "Direita (Right)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AureoleTheme.colors.surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            AureoleText(
                text = "Posição do Botão de Configurações",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.spacings.xSmall, horizontal = AureoleDS.spacings.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.spacings.small))
                        AureoleText(
                            text = label,
                            style = AureoleTheme.typography.bodyLarge,
                            color = AureoleTheme.colors.onSurfaceHigh
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText("Cancelar", color = AureoleTheme.colors.onSurfaceHigh)
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
internal fun SearchIconPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Left" to "Esquerda (Left)",
        "Right" to "Direita (Right)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AureoleTheme.colors.surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            AureoleText(
                text = "Posição do Ícone de Busca",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.spacings.xSmall, horizontal = AureoleDS.spacings.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.spacings.small))
                        AureoleText(
                            text = label,
                            style = AureoleTheme.typography.bodyLarge,
                            color = AureoleTheme.colors.onSurfaceHigh
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText("Cancelar", color = AureoleTheme.colors.onSurfaceHigh)
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
internal fun SidePanelPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Top" to "Superior (Top)",
        "Center" to "Centro (Center)",
        "Bottom" to "Inferior (Bottom)",
        "Space Evenly" to "Espaçamento Igual (Space Evenly)",
        "Space Between" to "Espaçamento Entre (Space Between)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AureoleTheme.colors.surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            AureoleText(
                text = "Alinhamento do Painel Lateral",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.spacings.xSmall, horizontal = AureoleDS.spacings.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.spacings.small))
                        AureoleText(
                            text = label,
                            style = AureoleTheme.typography.bodyLarge,
                            color = AureoleTheme.colors.onSurfaceHigh
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText("Cancelar", color = AureoleTheme.colors.onSurfaceHigh)
            }
        },
        modifier = Modifier.border(
            width = 0.5.dp,
            color = AureoleTheme.colors.outline,
            shape = RoundedCornerShape(22.dp)
        )
    )
}
