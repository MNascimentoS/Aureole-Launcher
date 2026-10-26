package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerFormCallbacks
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerFormState

@Composable
internal fun EditContainerOrientationSelector(
    orientation: String,
    onOrientationSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xSmall)
    ) {
        AureoleText(
            text = stringResource(R.string.edit_container_orientation),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            val isVertical = orientation.equals("Vertical", ignoreCase = true)
            val isHorizontal = orientation.equals("Horizontal", ignoreCase = true)

            listOf("Vertical" to isVertical, "Horizontal" to isHorizontal).forEach { (optText, isSelected) ->
                OrientationOptionButton(
                    optText = optText,
                    isSelected = isSelected,
                    onClick = { onOrientationSelected(optText) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OrientationOptionButton(
    optText: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleDS.colors.onSurfaceHigh,
                contentColor = AureoleDS.colors.surface
            )
        ) {
            AureoleText(text = optText)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            border = BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = 0.5f))
        ) {
            AureoleText(text = optText, color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}

@Composable
@ReadOnlyComposable
private fun getPositionOptionsMap(): List<Pair<String, String>> = listOf(
    "Space Between" to stringResource(R.string.edit_container_space_between),
    "Space Evenly" to stringResource(R.string.edit_container_space_evenly),
    "Top" to stringResource(R.string.edit_container_align_start),
    "Center" to stringResource(R.string.edit_container_align_center),
    "Bottom" to stringResource(R.string.edit_container_align_end)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditContainerAlignmentSelector(
    position: String,
    onPositionSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val positionOptionsMap = getPositionOptionsMap()
    val displayValue = positionOptionsMap.find { it.first == position }?.second ?: position

    Column {
        AureoleText(
            text = stringResource(R.string.edit_container_item_alignment),
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium, fontWeight = FontWeight.Bold)
        )
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
                    unfocusedBorderColor = AureoleDS.colors.outline,
                    focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                    unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
                ),
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            AlignmentDropdownMenu(
                expanded = isDropdownExpanded,
                selectedPosition = position,
                optionsMap = positionOptionsMap,
                onDismiss = { isDropdownExpanded = false },
                onPositionSelected = onPositionSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.AlignmentDropdownMenu(
    expanded: Boolean,
    selectedPosition: String,
    optionsMap: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onPositionSelected: (String) -> Unit
) {
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        containerColor = AureoleDS.colors.surfaceVariant,
        border = BorderStroke(
            width = AureoleDS.dimens.borderMinimal,
            color = AureoleDS.colors.outline.copy(alpha = 0.3f)
        ),
        shadowElevation = 8.dp,
        modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall)
    ) {
        optionsMap.forEach { (key, label) ->
            val isSelected = (key == selectedPosition)
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AureoleText(
                            text = label,
                            color = if (isSelected) {
                                AureoleDS.colors.onSurfaceHigh
                            } else {
                                AureoleDS.colors.onSurfaceMedium
                            },
                            format = AureoleTextFormatParams(
                                style = AureoleDS.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                        if (isSelected) {
                            AureoleDS.icons.Check(
                                tint = AureoleDS.colors.onSurfaceHigh,
                                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                            )
                        }
                    }
                },
                onClick = {
                    onPositionSelected(key)
                    onDismiss()
                },
                modifier = Modifier
                    .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.minimal)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                    .background(
                        if (isSelected) AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f) else Color.Transparent
                    )
            )
        }
    }
}

@Composable
internal fun EditContainerOptionsExpandableSection(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = AureoleDS.dimens.xxSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AureoleText(
                text = stringResource(R.string.edit_container_appearance_behavior_options),
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(AureoleDS.dimens.iconXLargeMedium)
            ) {
                if (isExpanded) {
                    AureoleDS.icons.ArrowUp(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                } else {
                    AureoleDS.icons.ArrowDown(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EditContainerSwitchesColumn(formState = formState, callbacks = callbacks)
        }
    }
}

@Composable
private fun EditContainerSwitchesColumn(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        modifier = Modifier.padding(top = AureoleDS.dimens.small)
    ) {
        EditContainerOrientationSelector(
            orientation = formState.orientation,
            onOrientationSelected = callbacks.onOrientationChange
        )

        EditContainerAlignmentSelector(
            position = formState.position,
            onPositionSelected = callbacks.onPositionChange
        )

        SettingSwitchRow(
            label = stringResource(R.string.edit_container_bg_label),
            checked = formState.isBackgroundEnabled,
            onCheckedChange = callbacks.onBgChange
        )

        SettingSwitchRow(
            label = stringResource(R.string.edit_container_expand_cell_label),
            checked = formState.isExpandCell,
            onCheckedChange = callbacks.onExpandChange
        )

        SettingSwitchRow(
            label = stringResource(R.string.edit_container_create_folder_btn),
            checked = formState.showAddFolderButton,
            onCheckedChange = callbacks.onAddFolderBtnChange
        )

        SettingSwitchRow(
            label = stringResource(R.string.edit_container_folder_labels),
            checked = formState.showFolderLabels,
            onCheckedChange = callbacks.onFolderLabelsChange
        )

        SettingSwitchRow(
            label = stringResource(R.string.edit_container_grid_folder_mode),
            checked = formState.isGridFolderEnabled,
            onCheckedChange = callbacks.onGridFolderChange
        )
    }
}

@Composable
internal fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@AureolePreview
@Composable
private fun EditContainerOrientationSelectorPreview() {
    AureoleLauncherTheme {
        EditContainerOrientationSelector(
            orientation = "Vertical",
            onOrientationSelected = {}
        )
    }
}
