package dev.mnascimentos.aureole.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.data.model.AppFolder
import dev.mnascimentos.aureole.data.model.AppInfo

@Composable
fun FolderDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    isLeftHandedMode: Boolean,
) {
    val appsInFolder = folder.appPackageNames.mapNotNull { pkgName ->
        allApps.find { it.packageName == pkgName }
    }.sortedBy { it.label.lowercase() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FolderDialogContent(
                    folder = folder,
                    appsInFolder = appsInFolder,
                    onAppClick = onAppClick,
                    isLeftHandedMode = isLeftHandedMode
                )
            }
        }
    }
}

private const val GRID_COLUMNS = 4

@Composable
private fun FolderDialogContent(
    folder: AppFolder,
    appsInFolder: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    isLeftHandedMode: Boolean
) {
    if (appsInFolder.isEmpty()) {
        Text(
            text = "Folder is empty",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 32.dp)
        )
    } else if (folder.displayAsGrid) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appsInFolder, key = { it.packageName }) { app ->
                Box(modifier = Modifier.padding(4.dp)) {
                    Text(app.label.take(1))
                }
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(appsInFolder, key = { it.packageName }) { app ->
                AppItemRow(
                    app = app,
                    onClick = { onAppClick(app) },
                    isLeftHandedMode = isLeftHandedMode
                )
            }
        }
    }
}
