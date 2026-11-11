package dev.mnascimentos.aureole.composable.dialogs.model

import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleFolderIcons
import dev.mnascimentos.aureole.core.designsystem.icons.FolderIconCategory

object FolderButtonMetrics {
    val SmallSize = 52.dp
    val MediumSize = 60.dp
    val LabelMaxWidth = 56.dp
    val ContainerMaxWidth = 68.dp
    val GridGap = 18.dp
}

/** Columns of the folder icon grid. */
const val FOLDER_ICON_COLUMNS = 5

/** Rows of icons visible at once (the half row hints that the grid scrolls); the grid scrolls inside itself. */
const val FOLDER_ICON_VISIBLE_ROWS = 2.5f

data class FolderFormConfig(
    val initialName: String,
    val initialIcon: String?,
    val isEditing: Boolean,
    val submitText: String
)

/** Name that is saved and shown in the preview: the typed text, trimmed, or [placeholder] when it is blank. */
fun resolveFolderName(raw: String, placeholder: String): String = raw.ifBlank { placeholder }.trim()

/** Keys of the icons of [category], in catalog order. */
fun folderIconKeys(category: FolderIconCategory): List<String> =
    AureoleFolderIcons.byCategory(category).map { it.key }

/** Keys of [category] split in rows of [FOLDER_ICON_COLUMNS]; the last row is not padded. */
fun folderIconRows(category: FolderIconCategory): List<List<String>> =
    folderIconKeys(category).chunked(FOLDER_ICON_COLUMNS)

/** Category shown first: the one of [selectedIcon] (old keys included), or the first category when none is set. */
fun initialFolderIconCategory(selectedIcon: String?): FolderIconCategory {
    val key = selectedIcon?.let(::resolveFolderIconKey)
    return AureoleFolderIcons.all.firstOrNull { it.key == key }?.category ?: FolderIconCategory.entries.first()
}

/** True when [iconKey] (or the old key it maps to) is the icon marked in the grid. */
fun isFolderIconSelected(iconKey: String, selectedIcon: String?): Boolean =
    selectedIcon != null && resolveFolderIconKey(selectedIcon) == iconKey

/** What the icon picker draws: the icon marked in the grid and the category whose icons are listed. */
data class FolderIconPickerState(
    val selectedIcon: String?,
    val category: FolderIconCategory
)

/** Callbacks of the icon picker. */
data class FolderIconPickerActions(
    val onIconSelected: (String) -> Unit,
    val onCategorySelected: (FolderIconCategory) -> Unit
)

/** What the sheet body of the folder form draws. */
data class FolderFormSheetState(
    val picker: FolderIconPickerState,
    val showDelete: Boolean
)

/** Callbacks of the sheet body of the folder form. */
data class FolderFormSheetActions(
    val picker: FolderIconPickerActions,
    val onDelete: (() -> Unit)?
)
