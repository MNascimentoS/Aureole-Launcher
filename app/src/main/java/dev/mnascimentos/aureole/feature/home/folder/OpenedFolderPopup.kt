package dev.mnascimentos.aureole.feature.home.folder

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHeaderBanner
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.folder.model.GridFolderPopupParams
import dev.mnascimentos.aureole.feature.home.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

private const val GRID_MAX_3 = 3
private const val GRID_MAX_6 = 6
private const val GRID_COLS = 3

@Composable
fun OpenedFolderPopup(
    config: OpenedFolderPopupConfig,
    modifier: Modifier = Modifier
) {
    val folder = config.folder
    val uiState = LocalHomeUiState.current
    val isGridMode = config.isGridFolderEnabled || folder.displayAsGrid

    val appsInFolder = remember(folder, config.allApps) {
        folder.appPackageNames.mapNotNull { pkgName ->
            config.allApps.find { it.packageName == pkgName }
        }
    }

    var showActionsByLongPress by remember { mutableStateOf(false) }
    val isActionsVisible = appsInFolder.isEmpty() || showActionsByLongPress

    if (isGridMode) {
        GridStyleExpandedFolderPopup(
            params = GridFolderPopupParams(
                folder = folder,
                appsInFolder = appsInFolder,
                actions = config.actions,
                isActionsVisible = isActionsVisible,
                onToggleActions = { showActionsByLongPress = !showActionsByLongPress },
                hazeState = config.hazeState,
                isHazeEnabled = uiState.isHazeEnabled
            ),
            modifier = modifier
        )
    } else {
        StandardFolderPopupBox(
            config = config,
            appsInFolder = appsInFolder,
            isActionsVisible = isActionsVisible,
            onToggleActions = { showActionsByLongPress = !showActionsByLongPress },
            modifier = modifier
        )
    }
}

@Composable
private fun StandardFolderPopupBox(
    config: OpenedFolderPopupConfig,
    appsInFolder: List<AppInfo>,
    isActionsVisible: Boolean,
    @Suppress("UNUSED_PARAMETER") onToggleActions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val homeActions = LocalHomeActions.current
    val context = LocalContext.current

    AureolePopupBox(
        hazeState = null,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .widthIn(min = 180.dp, max = 210.dp)
            .heightIn(max = 400.dp)
    ) {
        AureoleHeaderBanner(
            title = config.folder.name,
            onLogoClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            OpenedFolderActions(
                isVisible = isActionsVisible,
                onAddAppsClick = config.actions.onAddAppsClick,
                onEditFolderClick = config.actions.onEditFolderClick
            )

            if (config.folder.displayAsGrid) {
                OpenedFolderGrid(
                    folder = config.folder,
                    appsInFolder = appsInFolder,
                    onAppClick = config.actions.onAppClick
                )
            } else {
                OpenedFolderAppList(
                    appsInFolder = appsInFolder,
                    isLeftHandedMode = uiState.isLeftHandedMode,
                    onAppClick = config.actions.onAppClick,
                    onAppLongClick = { app -> homeActions.onOpenFolderAppBottomSheet(app, config.folder) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFolderGrid(
    folder: AppFolder,
    appsInFolder: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    val homeActions = LocalHomeActions.current
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum aplicativo na pasta",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val gridHeight = when {
            appsInFolder.size <= GRID_MAX_3 -> 110.dp
            appsInFolder.size <= GRID_MAX_6 -> 210.dp
            else -> 310.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLS),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = gridHeight)
        ) {
            items(appsInFolder, key = { it.packageName }) { app ->
                OpenedFolderGridAppItem(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { homeActions.onOpenFolderAppBottomSheet(app, folder) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderGridAppItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 6.dp, horizontal = 2.dp)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@AureolePreview
@Composable
fun OpenedFolderPopupPreview() {
    val mockApp = AppInfo(
        label = "Chat",
        packageName = "com.example.chat",
        componentName = ComponentName("com.example.chat", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockFolder = AppFolder(
        id = "1",
        name = "Social",
        appPackageNames = listOf(mockApp.packageName)
    )
    val actions = FolderPopupActions(
        onDismiss = {},
        onAppClick = {},
        onAddAppsClick = {},
        onEditFolderClick = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            OpenedFolderPopup(
                config = OpenedFolderPopupConfig(
                    folder = mockFolder,
                    allApps = listOf(mockApp),
                    actions = actions
                )
            )
        }
    }
}
