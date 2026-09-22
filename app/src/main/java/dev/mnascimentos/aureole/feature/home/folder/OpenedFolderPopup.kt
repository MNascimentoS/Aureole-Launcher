package dev.mnascimentos.aureole.feature.home.folder

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val HAZE_ALPHA_MULTIPLIER = 0.8f
private const val HAZE_MIN_ALPHA = 0.25f
private const val HAZE_MAX_ALPHA = 0.95f
private const val OPAQUE_ALPHA = 1f
private const val GRID_MAX_3 = 3
private const val GRID_MAX_6 = 6
private const val GRID_COLS = 3

data class OpenedFolderPopupConfig(
    val folder: AppFolder,
    val allApps: List<AppInfo>,
    val actions: FolderPopupActions,
    val isGridFolderEnabled: Boolean = false,
    val hazeState: HazeState? = null
)

data class OpenedFolderPopupContentParams(
    val folderName: String,
    val appsInFolder: List<AppInfo>,
    val isActionsVisible: Boolean,
    val isGridMode: Boolean
)

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
        }.sortedBy { it.label.lowercase() }
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
    onToggleActions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val isHazeEnabled = uiState.isHazeEnabled
    val hazeOpacity = uiState.hazeOpacity

    val hazeModifier = if (isHazeEnabled && (config.hazeState != null)) {
        Modifier.hazeEffect(
            state = config.hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val backgroundAlpha = if (isHazeEnabled) {
        (hazeOpacity * HAZE_ALPHA_MULTIPLIER).coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
    } else {
        OPAQUE_ALPHA
    }

    Box(
        modifier = modifier
            .widthIn(min = 210.dp, max = 250.dp)
            .heightIn(max = 380.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = backgroundAlpha))
            .padding(12.dp)
    ) {
        OpenedFolderPopupContent(
            params = OpenedFolderPopupContentParams(
                folderName = config.folder.name,
                appsInFolder = appsInFolder,
                isActionsVisible = isActionsVisible,
                isGridMode = false
            ),
            actions = config.actions,
            onToggleActions = onToggleActions
        )
    }
}

@Composable
private fun OpenedFolderPopupContent(
    params: OpenedFolderPopupContentParams,
    actions: FolderPopupActions,
    onToggleActions: () -> Unit
) {
    val isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode
    Column(modifier = Modifier.fillMaxWidth()) {
        OpenedFolderHeader(
            title = params.folderName,
            onDismiss = actions.onDismiss,
            onToggleActions = onToggleActions
        )

        Spacer(modifier = Modifier.height(8.dp))

        OpenedFolderActions(
            isVisible = params.isActionsVisible,
            onAddAppsClick = actions.onAddAppsClick,
            onEditFolderClick = actions.onEditFolderClick
        )

        if (params.isGridMode) {
            OpenedFolderGrid(
                appsInFolder = params.appsInFolder,
                onAppClick = actions.onAppClick
            )
        } else {
            OpenedFolderAppList(
                appsInFolder = params.appsInFolder,
                isLeftHandedMode = isLeftHandedMode,
                onAppClick = actions.onAppClick
            )
        }
    }
}

@Composable
private fun OpenedFolderGrid(
    appsInFolder: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
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
                    onClick = { onAppClick(app) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFolderGridAppItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderHeader(
    title: String,
    onDismiss: () -> Unit,
    onToggleActions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = {},
                    onLongClick = onToggleActions
                )
                .padding(vertical = 4.dp, horizontal = 4.dp)
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Folder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
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
