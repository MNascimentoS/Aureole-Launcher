package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderButtonMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderFormConfig

@Composable
fun FolderFormDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String?) -> Unit,
    modifier: Modifier = Modifier,
    folder: AppFolder? = null,
    onDelete: (() -> Unit)? = null
) {
    val isEditing = folder != null
    val title =
        if (isEditing) {
            stringResource(
                R.string.home_folder_edit_title
            )
        } else {
            stringResource(R.string.home_folder_new_title)
        }
    val submitText =
        if (isEditing) stringResource(R.string.core_common_save) else stringResource(R.string.core_common_create)

    AureoleDialog(
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier
    ) {
        FolderFormDialogContent(
            config = FolderFormConfig(
                initialName = folder?.name.orEmpty(),
                initialIcon = folder?.icon,
                isEditing = isEditing,
                submitText = submitText
            ),
            onDismiss = onDismiss,
            onSave = onSave,
            onDelete = onDelete
        )
    }
}

@Composable
private fun FolderFormDialogContent(
    config: FolderFormConfig,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String?) -> Unit,
    onDelete: (() -> Unit)?
) {
    var folderName by remember { mutableStateOf(config.initialName) }
    var selectedIcon by remember { mutableStateOf(config.initialIcon) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FolderIconPreview(folderName = folderName, selectedIcon = selectedIcon)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        FolderNameTextField(
            folderName = folderName,
            onFolderNameChange = { folderName = it }
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        AureoleText(
            text = stringResource(dev.mnascimentos.aureole.R.string.home_folder_icon),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.align(Alignment.Start),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))

        IconSelectionRow(
            selectedIcon = selectedIcon,
            onIconSelected = { selectedIcon = it },
            folderName = folderName
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

        val placeholderName = stringResource(R.string.home_folder_name_placeholder)
        FolderFormButtons(
            isEditing = config.isEditing,
            submitText = config.submitText,
            onDelete = onDelete,
            onDismiss = onDismiss,
            onSave = {
                val finalName = folderName.ifBlank { placeholderName }
                onSave(finalName.trim(), selectedIcon)
            }
        )
    }
}

@Composable
private fun FolderNameTextField(
    folderName: String,
    onFolderNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = folderName,
        onValueChange = onFolderNameChange,
        label = {
            AureoleText(
                stringResource(R.string.home_folder_name_label),
                color = AureoleDS.colors.onSurfaceMedium
            )
        },
        placeholder = {
            AureoleText(
                stringResource(R.string.home_folder_name_placeholder),
                color = AureoleDS.colors.onSurfaceLow
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedBorderColor = AureoleDS.colors.outline,
            focusedTextColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FolderIconPreview(
    folderName: String,
    selectedIcon: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(FolderButtonMetrics.SmallSize)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (selectedIcon != null) {
            FolderIconRegistry.RenderIcon(
                name = selectedIcon,
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        } else {
            AureoleText(
                text = folderName.take(1).uppercase().ifEmpty { "?" },
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun IconSelectionRow(
    selectedIcon: String?,
    onIconSelected: (String?) -> Unit,
    folderName: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultLetterOptionChip(
            isSelected = selectedIcon == null,
            letter = folderName.take(1).uppercase().ifEmpty { "A" },
            onClick = { onIconSelected(null) }
        )

        FolderIconRegistry.icons.keys.forEach { iconKey ->
            IconOptionChip(
                isSelected = selectedIcon == iconKey,
                iconKey = iconKey,
                onClick = { onIconSelected(iconKey) }
            )
        }
    }
}

@Composable
private fun DefaultLetterOptionChip(
    isSelected: Boolean,
    letter: String,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.outline
    }
    val textColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .background(
                if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant
            )
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = letter,
            color = textColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun IconOptionChip(
    isSelected: Boolean,
    iconKey: String,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.outline
    }
    val iconTint: Color = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .background(
                if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant
            )
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center
    ) {
        FolderIconRegistry.RenderIcon(
            name = iconKey,
            tint = iconTint,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}

@Composable
private fun FolderFormButtons(
    isEditing: Boolean,
    submitText: String,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing && onDelete != null) {
            TextButton(onClick = onDelete) {
                AureoleText(
                    stringResource(dev.mnascimentos.aureole.R.string.core_common_delete),
                    color = AureoleDS.colors.error
                )
            }
        } else {
            Spacer(modifier = Modifier.width(AureoleDS.dimens.borderMinimal))
        }

        Row {
            TextButton(onClick = onDismiss) {
                AureoleText(
                    stringResource(dev.mnascimentos.aureole.R.string.core_common_cancel),
                    color = AureoleDS.colors.onSurfaceMedium
                )
            }
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            TextButton(onClick = onSave) {
                AureoleText(
                    submitText,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun FolderFormDialogNewPreview() {
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = null,
            onDismiss = {},
            onSave = { _, _ -> }
        )
    }
}

@AureolePreview
@Composable
private fun FolderFormDialogEditPreview() {
    val mockFolder = AppFolder(id = "1", name = "Tools")
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = mockFolder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}
