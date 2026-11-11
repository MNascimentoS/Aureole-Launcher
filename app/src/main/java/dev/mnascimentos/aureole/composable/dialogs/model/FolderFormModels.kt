package dev.mnascimentos.aureole.composable.dialogs.model

import androidx.compose.ui.unit.dp

object FolderButtonMetrics {
    val SmallSize = 52.dp
    val MediumSize = 60.dp
    val LabelMaxWidth = 56.dp
    val ContainerMaxWidth = 68.dp
    val GridGap = 18.dp
}

/** Columns of the folder icon grid. */
const val FOLDER_ICON_COLUMNS = 7

data class FolderFormConfig(
    val initialName: String,
    val initialIcon: String?,
    val isEditing: Boolean,
    val submitText: String
)

/** Name that is saved and shown in the preview: the typed text, trimmed, or [placeholder] when it is blank. */
fun resolveFolderName(raw: String, placeholder: String): String = raw.ifBlank { placeholder }.trim()

/**
 * Options of the icon grid in order: the first letter of the name (null) and then every icon of [iconKeys].
 * Split in rows of [FOLDER_ICON_COLUMNS]; the last row is not padded.
 */
fun folderIconRows(iconKeys: Collection<String>): List<List<String?>> =
    (listOf<String?>(null) + iconKeys).chunked(FOLDER_ICON_COLUMNS)

/** What the sheet body of the folder form draws. */
data class FolderFormSheetState(
    val folderName: String,
    val selectedIcon: String?,
    val showDelete: Boolean
)

/** Callbacks of the sheet body of the folder form. */
data class FolderFormSheetActions(
    val onIconSelected: (String?) -> Unit,
    val onDelete: (() -> Unit)?
)
