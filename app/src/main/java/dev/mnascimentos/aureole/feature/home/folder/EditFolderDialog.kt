package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun EditFolderDialog(
    folder: AppFolder,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String?) -> Unit,
    onDelete: () -> Unit
) {
    FolderFormDialog(
        folder = folder,
        onDismiss = onDismiss,
        onSave = onSave,
        onDelete = onDelete
    )
}

@AureolePreview
@Composable
fun EditFolderDialogPreview() {
    val mockFolder = AppFolder(id = "1", name = "Tools")
    AureoleLauncherTheme {
        EditFolderDialog(
            folder = mockFolder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}
