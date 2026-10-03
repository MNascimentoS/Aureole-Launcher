package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 340.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    width = 0.5.dp,
                    color = AureoleTheme.colors.outline,
                    shape = RoundedCornerShape(22.dp)
                )
                .background(AureoleTheme.colors.surface)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CreateFolderHeader()

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
}

@Composable
private fun CreateFolderHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AureoleText(
            text = "New Folder",
            style = AureoleTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        AureoleText(
            text = "Folder is empty",
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
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
