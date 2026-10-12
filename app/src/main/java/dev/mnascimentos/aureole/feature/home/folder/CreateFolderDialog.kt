package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, icon: String?) -> Unit
) {
    FolderFormDialog(
        folder = null,
        onDismiss = onDismiss,
        onSave = onSubmit
    )
}

@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    CreateFolderDialog(
        onDismiss = onDismiss,
        onSubmit = { name, _ -> onSubmit(name) }
    )
}

@AureolePreview
@Composable
fun CreateFolderDialogPreview() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = { _, _ -> }
        )
    }
}
