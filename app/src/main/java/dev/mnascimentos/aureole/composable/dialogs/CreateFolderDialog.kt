package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderFormDialog

@NonRestartableComposable
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

@NonRestartableComposable
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
private fun CreateFolderDialogPreview() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = { _, _ -> }
        )
    }
}
