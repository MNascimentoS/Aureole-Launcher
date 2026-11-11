package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_ICON_COLUMNS
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconRows
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
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

/**
 * Icon options in a grid of [FOLDER_ICON_COLUMNS] columns: the first letter of [folderName] and every folder icon.
 * Mirrored for the left-handed mode so the first options sit under the thumb.
 */
@Composable
fun FolderIconGrid(
    selectedIcon: String?,
    onIconSelected: (String?) -> Unit,
    folderName: String,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            folderIconRows(FolderIconRegistry.pickerKeys).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                    row.forEach { iconKey ->
                        IconTile(
                            icon = iconKey,
                            letter = folderName.take(1).uppercase().ifEmpty { "A" },
                            isSelected = selectedIcon == iconKey,
                            onClick = { onIconSelected(iconKey) }
                        )
                    }
                    repeat(FOLDER_ICON_COLUMNS - row.size) { EmptyTile() }
                }
            }
        }
    }
}

@Composable
private fun RowScope.IconTile(icon: String?, letter: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.iconXLarge)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant)
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.zero,
                color = AureoleDS.colors.onSurfaceHigh,
                shape = shape
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            FolderIconRegistry.RenderIcon(
                name = icon,
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        } else {
            AureoleText(
                text = letter,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun RowScope.EmptyTile() {
    Box(modifier = Modifier.weight(1f))
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
private fun FolderIconGridPreview() {
    AureoleLauncherTheme {
        FolderIconGrid(selectedIcon = null, onIconSelected = {}, folderName = "Work")
    }
}
