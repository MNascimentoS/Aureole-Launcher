package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** The folder as it will look on the Home: the chosen icon (or the first letter of the name) and the name below. */
@Composable
fun FolderIconPreview(
    folderName: String,
    selectedIcon: String?,
    modifier: Modifier = Modifier,
    label: String = folderName
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(FolderButtonMetrics.SmallSize)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            FolderTileContent(icon = selectedIcon, letter = label.take(1).uppercase().ifEmpty { "A" })
        }
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(top = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelMedium,
                maxLines = 1
            )
        )
    }
}

@Composable
private fun FolderTileContent(icon: String?, letter: String) {
    if (icon != null) {
        FolderIconRegistry.RenderIcon(
            name = icon,
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    } else {
        AureoleText(
            text = letter,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/** Name field of the folder form; sits glued above the action row, so it stays next to the keyboard. */
@Composable
fun FolderNameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            AureoleText(
                stringResource(R.string.home_folder_name_label),
                color = AureoleDS.colors.onSurfaceLow
            )
        },
        leadingIcon = {
            AureoleDS.icons.Edit(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedBorderColor = AureoleDS.colors.outline,
            focusedTextColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
private fun FolderNameFieldPreview() {
    AureoleLauncherTheme {
        FolderNameField(value = "Work", onValueChange = {})
    }
}
