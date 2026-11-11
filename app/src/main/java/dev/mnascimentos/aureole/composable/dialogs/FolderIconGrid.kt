package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_ICON_COLUMNS
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_ICON_VISIBLE_ROWS
import dev.mnascimentos.aureole.composable.dialogs.model.FolderIconPickerActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderIconPickerState
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconRows
import dev.mnascimentos.aureole.composable.dialogs.model.isFolderIconSelected
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.FolderIconCategory
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Icon picker of the folder form: category chips on top and the icons of the chosen category in a grid of
 * [FOLDER_ICON_COLUMNS] columns. The saved icon is outlined. Mirrored for the left-handed mode so the
 * chips and the first icons sit under the thumb.
 */
@Composable
fun FolderIconGrid(
    state: FolderIconPickerState,
    actions: FolderIconPickerActions,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            FolderCategoryChips(category = state.category, onCategorySelected = actions.onCategorySelected)
            FolderIconRows(state = state, actions = actions)
        }
    }
}

/** The icons of the category: capped at [FOLDER_ICON_VISIBLE_ROWS] rows and scrolling on their own beyond that. */
@Composable
private fun FolderIconRows(state: FolderIconPickerState, actions: FolderIconPickerActions) {
    val gap = AureoleDS.dimens.xSmall
    Column(
        modifier = Modifier
            .heightIn(max = AureoleDS.dimens.iconXLarge * FOLDER_ICON_VISIBLE_ROWS + gap * 2)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        folderIconRows(state.category).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { iconKey ->
                    IconTile(
                        icon = iconKey,
                        isSelected = isFolderIconSelected(iconKey, state.selectedIcon),
                        onClick = { actions.onIconSelected(iconKey) }
                    )
                }
                repeat(FOLDER_ICON_COLUMNS - row.size) { EmptyTile() }
            }
        }
    }
}

@Composable
private fun FolderCategoryChips(
    category: FolderIconCategory,
    onCategorySelected: (FolderIconCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        FolderIconCategory.entries.forEach { entry ->
            CategoryChip(
                label = stringResource(entry.label),
                isSelected = entry == category,
                onClick = { onCategorySelected(entry) }
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected }
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xxSmall),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, maxLines = 1)
        )
    }
}

@Composable
private fun RowScope.IconTile(icon: String, isSelected: Boolean, onClick: () -> Unit) {
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
        FolderIconRegistry.RenderIcon(
            name = icon,
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}

@Composable
private fun RowScope.EmptyTile() {
    Box(modifier = Modifier.weight(1f))
}

@AureolePreview
@Composable
private fun FolderIconGridPreview() {
    AureoleLauncherTheme {
        FolderIconGrid(
            state = FolderIconPickerState(selectedIcon = "work", category = FolderIconCategory.WORK),
            actions = FolderIconPickerActions(onIconSelected = {}, onCategorySelected = {}),
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
