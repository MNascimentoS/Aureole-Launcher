package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }

    AureoleDialog(
        onDismissRequest = onDismiss,
        title = "New Folder"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AureoleText(
                text = "Folder is empty",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { AureoleText("Folder Name", color = AureoleTheme.colors.onSurfaceMedium) },
                placeholder = { AureoleText("New Folder", color = AureoleTheme.colors.onSurfaceLow) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = AureoleTheme.colors.outline,
                    focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                    unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            CreateFolderButtons(
                onDismiss = onDismiss,
                onSubmit = {
                    val finalName = folderName.ifBlank { "New Folder" }
                    onSubmit(finalName)
                }
            )
        }
    }
}

@Composable
private fun CreateFolderButtons(
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismiss) {
            AureoleText("Cancel", color = AureoleTheme.colors.onSurfaceMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onSubmit) {
            AureoleText("Create", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@AureolePreview
@Composable
fun CreateFolderDialogPreview() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = {}
        )
    }
}
