package dev.mnascimentos.aureole.feature.settings.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

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
        containerColor = AureoleDS.colors.surface,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge),
        title = {
            AureoleText(
                text = stringResource(R.string.settings_dialogs_settings_button_position_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
                        AureoleText(
                            text = label,
                            color = AureoleDS.colors.onSurfaceHigh,
                            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(stringResource(R.string.core_common_cancel), color = AureoleDS.colors.onSurfaceHigh)
            }
        }
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
        containerColor = AureoleDS.colors.surface,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge),
        title = {
            AureoleText(
                text = stringResource(R.string.settings_dialogs_search_icon_position_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
                        AureoleText(
                            text = label,
                            color = AureoleDS.colors.onSurfaceHigh,
                            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(stringResource(R.string.core_common_cancel), color = AureoleDS.colors.onSurfaceHigh)
            }
        }
    )
}

@Composable
internal fun ContainerPositionDialog(
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
        containerColor = AureoleDS.colors.surface,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge),
        title = {
            AureoleText(
                text = "Alinhamento do Painel Lateral",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
                        AureoleText(
                            text = label,
                            color = AureoleDS.colors.onSurfaceHigh,
                            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(stringResource(R.string.core_common_cancel), color = AureoleDS.colors.onSurfaceHigh)
            }
        }
    )
}

@AureolePreview
@Composable
private fun ContainerPositionDialogPreview() {
    AureoleLauncherTheme {
        ContainerPositionDialog(
            currentPosition = "Center",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@AureolePreview
@Composable
private fun SettingsButtonPositionDialogPreview() {
    AureoleLauncherTheme {
        SettingsButtonPositionDialog(
            currentPosition = "Right",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}

@AureolePreview
@Composable
private fun SearchIconPositionDialogPreview() {
    AureoleLauncherTheme {
        SearchIconPositionDialog(
            currentPosition = "Left",
            onPositionSelected = {},
            onDismiss = {}
        )
    }
}
