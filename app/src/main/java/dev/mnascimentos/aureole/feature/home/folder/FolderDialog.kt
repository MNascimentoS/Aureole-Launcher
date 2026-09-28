package dev.mnascimentos.aureole.feature.home.folder

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.AppItemRow
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun FolderDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
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
                AureoleText(
                    text = folder.name,
                    style = AureoleTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FolderDialogContent(
                    folder = folder,
                    appsInFolder = appsInFolder,
                    onAppClick = onAppClick,
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
) {
    if (appsInFolder.isEmpty()) {
        AureoleText(
            text = "Folder is empty",
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceMedium,
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
                    AureoleText(app.label.take(1), color = AureoleTheme.colors.onSurfaceHigh)
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
                )
            }
        }
    }
}

@AureolePreview
@Composable
fun FolderDialogPreview() {
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockFolder = AppFolder(
        id = "1",
        name = "Media",
        appPackageNames = listOf(mockApp.packageName)
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            FolderDialog(
                folder = mockFolder,
                allApps = listOf(mockApp),
                onDismiss = {},
                onAppClick = {}
            )
        }
    }
}
