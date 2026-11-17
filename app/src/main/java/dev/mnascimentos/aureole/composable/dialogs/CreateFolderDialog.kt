package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Sheet that creates a folder. [pickApps] labels its button "Create and pick apps" (the caller opens the app picker
 * of the new folder); false labels it "Create".
 */
@NonRestartableComposable
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onSubmit: (FolderDetails) -> Unit,
    pickApps: Boolean = true
) {
    FolderFormDialog(
        folder = null,
        onDismiss = onDismiss,
        onSave = onSubmit,
        pickAppsOnCreate = pickApps
    )
}

@AureolePreview
@Composable
private fun CreateFolderDialogPreview() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = {}
        )
    }
}
