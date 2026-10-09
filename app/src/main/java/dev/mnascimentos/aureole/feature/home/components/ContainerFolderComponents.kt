package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.ContainerFolderButtonParams
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry

private const val MAX_PREVIEW_APPS = 9
private const val GRID_ROWS = 3
private const val GRID_COLS = 3
private const val FOLDER_BG_INACTIVE_ALPHA = 0.85f

@Composable
fun ContainerAddFolderButton(
    onClick: () -> Unit,
    isBackgroundEnabled: Boolean = true
) {
    val buttonSize = if (isBackgroundEnabled) 52.dp else 60.dp
    val iconSize = if (isBackgroundEnabled) 28.dp else 34.dp
    val cornerRadius = if (isBackgroundEnabled) 15.dp else 18.dp

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .size(buttonSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Add(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun ContainerFolderItem(
    folder: AppFolder,
    isOpened: Boolean,
    showFolderLabels: Boolean,
    isGridFolderEnabled: Boolean,
    isBackgroundEnabled: Boolean = true,
    onFolderClick: (AppFolder, Float) -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    var itemYInWindow by remember { mutableFloatStateOf(0f) }

    val containerColor by animateColorAsState(
        targetValue = if (isOpened) {
            AureoleDS.colors.onSurfaceHigh
        } else {
            AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA)
        },
        label = "folder_bg"
    )
    val textColor = if (isOpened) {
        AureoleDS.colors.surface
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(max = 68.dp),
    ) {
        ContainerFolderButton(
            params = ContainerFolderButtonParams(
                folder = folder,
                isGridFolderEnabled = isGridFolderEnabled,
                containerColor = containerColor,
                textColor = textColor
            ),
            isBackgroundEnabled = isBackgroundEnabled,
            onPositionedY = { y -> itemYInWindow = y },
            onClick = { onFolderClick(folder, itemYInWindow) },
            onLongClick = onLongClick
        )

        if (showFolderLabels) {
            ContainerFolderLabel(name = folder.name)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContainerFolderButton(
    params: ContainerFolderButtonParams,
    onPositionedY: (Float) -> Unit,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isBackgroundEnabled: Boolean = true
) {
    val uiState = LocalHomeUiState.current
    val showGridPreview = params.isGridFolderEnabled || params.folder.displayAsGrid
    val buttonSize = if (isBackgroundEnabled) 52.dp else 60.dp
    val iconSize = if (isBackgroundEnabled) 28.dp else 34.dp
    val cornerRadius = if (isBackgroundEnabled) 15.dp else 18.dp

    val clickModifier = if (onLongClick != null) {
        Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    } else {
        Modifier.clickable(onClick = onClick)
    }

    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(params.containerColor)
            .onGloballyPositioned { coordinates ->
                onPositionedY(coordinates.positionInWindow().y)
            }
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        if (showGridPreview) {
            FolderMiniGridPreview(
                folder = params.folder,
                allApps = uiState.apps,
                modifier = Modifier.size(buttonSize)
            )
        } else {
            if (params.folder.icon != null) {
                FolderIconRegistry.RenderIcon(
                    name = params.folder.icon,
                    modifier = Modifier.size(iconSize),
                    tint = params.textColor
                )
            } else {
                AureoleText(
                    text = params.folder.name.take(1).uppercase(),
                    color = params.textColor,
                    style = if (isBackgroundEnabled) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FolderMiniGridPreview(
    folder: AppFolder,
    allApps: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val folderApps = remember(folder.appPackageNames, allApps) {
        folder.appPackageNames.take(MAX_PREVIEW_APPS).mapNotNull { pkg ->
            allApps.find { it.packageName == pkg }
        }
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.85f))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        if (folderApps.isEmpty()) {
            EmptyFolderMiniGridPreview(folder = folder)
        } else {
            PopulatedFolderMiniGrid(folderApps = folderApps)
        }
    }
}

@Composable
private fun EmptyFolderMiniGridPreview(folder: AppFolder) {
    if (folder.icon != null) {
        FolderIconRegistry.RenderIcon(
            name = folder.icon,
            modifier = Modifier.size(24.dp),
            tint = AureoleDS.colors.onSurfaceMedium
        )
    } else {
        AureoleText(
            text = folder.name.take(1).uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PopulatedFolderMiniGrid(folderApps: List<AppInfo>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until GRID_ROWS) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until GRID_COLS) {
                    val index = row * GRID_COLS + col
                    if (index < folderApps.size) {
                        val app = folderApps[index]
                        val bitmap = remember(app.packageName) { app.getIconBitmap() }
                        Image(
                            bitmap = bitmap,
                            contentDescription = app.label,
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ContainerFolderLabel(name: String) {
    Spacer(modifier = Modifier.height(2.dp))
    AureoleText(
        text = name,
        color = AureoleDS.colors.onSurfaceHigh,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 56.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContainerAppItem(
    app: AppInfo,
    showLabels: Boolean,
    isBackgroundEnabled: Boolean = true,
    onAppClick: (AppInfo) -> Unit,
    onLongClick: () -> Unit
) {
    val uiState = LocalHomeUiState.current
    val bitmap = app.getDisplayIconBitmap(
        isThemed = uiState.isThemedAppIconsEnabled,
        tintColor = AureoleDS.colors.onSurfaceMedium
    )
    val buttonSize = if (isBackgroundEnabled) 52.dp else 60.dp
    val cornerRadius = if (isBackgroundEnabled) 15.dp else 18.dp
    val imgPadding = if (isBackgroundEnabled) 3.dp else 2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(max = 68.dp),
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA))
                .combinedClickable(
                    onClick = { onAppClick(app) },
                    onLongClick = onLongClick
                )
                .padding(imgPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showLabels) {
            ContainerFolderLabel(name = app.label)
        }
    }
}
