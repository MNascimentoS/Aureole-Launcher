package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHeaderBanner
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemRow
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

private object FolderDialogMetrics {
    val MinWidth = 280.dp
    val MaxWidth = 340.dp
}

@Composable
fun FolderDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appsInFolder = folder.appPackageNames.mapNotNull { pkgName ->
        allApps.find { it.packageName == pkgName }
    }
    val context = LocalContext.current

    val dialogContent = @Composable {
        AureolePopupBox(
            modifier = modifier.widthIn(min = FolderDialogMetrics.MinWidth, max = FolderDialogMetrics.MaxWidth)
        ) {
            AureoleHeaderBanner(
                title = folder.name,
                onLogoClick = {
                    onDismiss()
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
            ) {
                FolderDialogContent(
                    folder = folder,
                    appsInFolder = appsInFolder,
                    onAppClick = onAppClick,
                )
            }
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            dialogContent()
        }
    } else {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            dialogContent()
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
            text = stringResource(R.string.folder_is_empty),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(vertical = AureoleDS.dimens.xLarge),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    } else if (folder.displayAsGrid) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            contentPadding = PaddingValues(vertical = AureoleDS.dimens.xSmall),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            items(
                items = appsInFolder,
                key = { it.packageName },
                contentType = { "folder_app_grid" }
            ) { app ->
                Box(modifier = Modifier.padding(AureoleDS.dimens.xxSmall)) {
                    AureoleText(app.label.take(1), color = AureoleDS.colors.onSurfaceHigh)
                }
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = AureoleDS.dimens.xSmall)
        ) {
            items(
                items = appsInFolder,
                key = { it.packageName },
                contentType = { "folder_app_row" }
            ) { app ->
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
private fun FolderDialogPreview() {
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
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
